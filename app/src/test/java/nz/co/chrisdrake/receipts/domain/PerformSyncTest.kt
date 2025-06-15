package nz.co.chrisdrake.receipts.domain

import android.net.ConnectivityManager
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import nz.co.chrisdrake.receipts.data.ReceiptRepository
import nz.co.chrisdrake.receipts.data.RemoteDataSource
import nz.co.chrisdrake.receipts.data.UserPreferencesRepository
import nz.co.chrisdrake.receipts.domain.auth.GetCurrentUser
import nz.co.chrisdrake.receipts.domain.image.CreateImageFilePaths
import nz.co.chrisdrake.receipts.domain.model.ReceiptImageDownloadPaths
import nz.co.chrisdrake.receipts.domain.model.ReceiptImageFilePaths
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argWhere
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.io.File

class PerformSyncTest {

    private val remoteReceipt = createReceipt(
        imageDownloadPaths = ReceiptImageDownloadPaths(
            original = "remote/original.jpg",
            thumbnail = "remote/thumb.jpg",
        ),
        updatedAt = 1000L,
    )
    private val imageFilePaths = ReceiptImageFilePaths(
        original = "local/original.jpg",
        thumbnail = "local/thumb.jpg",
    )
    private val user = createUser()

    private val getCurrentUser: GetCurrentUser = mock()
    private val createImageFilePaths: CreateImageFilePaths = mock()
    private val remoteDataSource: RemoteDataSource = mock()
    private val receiptRepository: ReceiptRepository = mock()
    private val userPreferencesRepository: UserPreferencesRepository = mock()
    private var connectivityManager: ConnectivityManager = connectivityManager(unmetered = true)

    private val performSync: PerformSync by lazy {
        PerformSync(
            getCurrentUser = getCurrentUser,
            createImageFilePaths = createImageFilePaths,
            remoteDataSource = remoteDataSource,
            receiptRepository = receiptRepository,
            userPreferencesRepository = userPreferencesRepository,
            connectivityManager = connectivityManager,
        )
    }

    @Before
    fun setup() = runTest {
        whenever(getCurrentUser()).thenReturn(user)
        whenever(userPreferencesRepository.getLastSyncTime()).thenReturn(flowOf(1L))
        whenever(remoteDataSource.getReceipts(userId = user.id, updatedAfter = 1L))
            .thenReturn(listOf(remoteReceipt))
        whenever(receiptRepository.getReceipt(id = remoteReceipt.id))
            .thenReturn(remoteReceipt.copy(updatedAt = 999L))
        whenever(createImageFilePaths(receiptId = remoteReceipt.id)).thenReturn(imageFilePaths)
    }

    @Test
    fun `syncs receipts and saves last sync time`() = runTest {
        performSync().join()

        inOrder(receiptRepository, userPreferencesRepository, remoteDataSource) {
            verify(remoteDataSource, times(2)).getImage(path = any(), destinationFile = any())
            verify(receiptRepository).saveReceipt(argWhere { it.id == remoteReceipt.id })
            verify(userPreferencesRepository).saveLastSyncTime(any())
        }
        verify(remoteDataSource).getImage(path = "remote/original.jpg", destinationFile = File("local/original.jpg"))
        verify(remoteDataSource).getImage(path = "remote/thumb.jpg", destinationFile = File("local/thumb.jpg"))
    }

    @Test
    fun `does nothing if no user`() = runTest {
        whenever(getCurrentUser()).thenReturn(null)

        performSync().join()

        verifyNoInteractions(userPreferencesRepository, remoteDataSource, receiptRepository)
    }

    @Test
    fun `does nothing if identical receipt already stored locally`() = runTest {
        whenever(receiptRepository.getReceipt(id = remoteReceipt.id)).thenReturn(remoteReceipt)

        performSync().join()

        verify(remoteDataSource, never()).getImage(any(), any())
        verify(receiptRepository, never()).saveReceipt(any())
        verify(userPreferencesRepository).saveLastSyncTime(any())
    }

    @Test
    fun `does nothing if connection is metered`() = runTest {
        connectivityManager = connectivityManager(unmetered = false)

        performSync().join()

        verify(remoteDataSource, never()).getImage(any(), any())
        verify(receiptRepository, never()).saveReceipt(any())
        verify(userPreferencesRepository).saveLastSyncTime(any())
    }
}
