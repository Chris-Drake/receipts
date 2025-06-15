package nz.co.chrisdrake.receipts.domain

import android.net.ConnectivityManager
import kotlinx.coroutines.test.runTest
import nz.co.chrisdrake.receipts.data.ReceiptRepository
import nz.co.chrisdrake.receipts.domain.auth.GetCurrentUser
import nz.co.chrisdrake.receipts.domain.model.BackupStatus
import nz.co.chrisdrake.receipts.domain.model.User
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BackupReceiptsAsyncTest {

    private val notBackedUpReceipt = createReceipt(id = "123", backupStatus = BackupStatus.NotStarted)
    private val failedReceipt = createReceipt(id = "456", backupStatus = BackupStatus.Failed)
    private val backedUpReceipt = createReceipt(id = "789", backupStatus = BackupStatus.Completed)

    private val getCurrentUser: GetCurrentUser = mock()
    private val receiptRepository: ReceiptRepository = mock()
    private val user: User = createUser()
    private var connectivityManager: ConnectivityManager = connectivityManager(unmetered = true)

    private val backupReceiptsAsync: BackupReceiptsAsync by lazy {
        BackupReceiptsAsync(
            getCurrentUser = getCurrentUser,
            receiptRepository = receiptRepository,
            connectivityManager = connectivityManager,
        )
    }

    @Before
    fun setUp() = runTest {
        whenever(getCurrentUser()).thenReturn(user)
    }

    @Test
    fun `backs up pending receipts and resets incomplete status on first run`() = runTest {
        val receipts = listOf(notBackedUpReceipt, failedReceipt, backedUpReceipt)

        backupReceiptsAsync(receipts).join()

        inOrder(receiptRepository) {
            verify(receiptRepository)
                .updateReceipts(listOf(failedReceipt.copy(backUpStatus = BackupStatus.NotStarted)))
            verify(receiptRepository).backupReceipt(userId = user.id, id = notBackedUpReceipt.id)
            verify(receiptRepository).backupReceipt(userId = user.id, id = failedReceipt.id)
        }
    }

    @Test
    fun `does nothing if no user`() = runTest {
        whenever(getCurrentUser()).thenReturn(null)

        backupReceiptsAsync(listOf(notBackedUpReceipt, failedReceipt)).join()

        verify(receiptRepository, never()).backupReceipt(userId = any(), id = any())
    }

    @Test
    fun `does nothing if connection is metered`() = runTest {
        connectivityManager = connectivityManager(unmetered = false)

        backupReceiptsAsync(listOf(notBackedUpReceipt, failedReceipt)).join()

        verify(receiptRepository, never()).backupReceipt(userId = any(), id = any())
    }
}
