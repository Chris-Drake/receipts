package nz.co.chrisdrake.receipts.domain

import android.net.ConnectivityManager
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import nz.co.chrisdrake.receipts.data.ReceiptRepository
import nz.co.chrisdrake.receipts.data.RemoteDataSource
import nz.co.chrisdrake.receipts.data.UserPreferencesRepository
import nz.co.chrisdrake.receipts.domain.auth.GetCurrentUser
import nz.co.chrisdrake.receipts.domain.image.CreateImageFilePaths
import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.util.isUnmeteredNetwork
import java.io.File

class PerformSync(
    private val getCurrentUser: GetCurrentUser,
    private val createImageFilePaths: CreateImageFilePaths,
    private val remoteDataSource: RemoteDataSource,
    private val receiptRepository: ReceiptRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val connectivityManager: ConnectivityManager,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex()

    operator fun invoke() = scope.launch {
        mutex.withLock {
            val userId = getCurrentUser()?.id ?: return@launch

            // TODO: Start a foreground notification

            try {
                val lastSyncTime = userPreferencesRepository.getLastSyncTime().first()

                remoteDataSource
                    .getReceipts(userId = userId, updatedAfter = lastSyncTime)
                    .forEach { receipt ->
                        if (isActive && connectivityManager.isUnmeteredNetwork()) {
                            receiptRepository.getReceipt(id = receipt.id)
                                ?.takeUnless { it.updatedAt < receipt.updatedAt }
                                ?: import(receipt)
                        }
                    }

                userPreferencesRepository.saveLastSyncTime(System.currentTimeMillis())
            } catch (cancellation: Exception) {
                throw cancellation
            } catch (exception: Exception) {
                Firebase.crashlytics.recordException(exception)
            }
        }
    }

    private suspend fun import(receipt: Receipt) = coroutineScope {
        val imagePaths = createImageFilePaths(receiptId = receipt.id)
        val imageDownloadPaths = checkNotNull(receipt.imageDownloadPaths)

        awaitAll(
            async {
                remoteDataSource
                    .getImage(imageDownloadPaths.original, File(checkNotNull(imagePaths.original)))
            },
            async {
                remoteDataSource
                    .getImage(imageDownloadPaths.thumbnail, File(imagePaths.thumbnail))
            }
        )

        receiptRepository.saveReceipt(receipt.copy(imageFilePaths = imagePaths))
    }
}
