package nz.co.chrisdrake.receipts.domain

import android.net.ConnectivityManager
import androidx.core.net.toUri
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import nz.co.chrisdrake.receipts.data.ReceiptRepository
import nz.co.chrisdrake.receipts.data.RemoteDataSource
import nz.co.chrisdrake.receipts.domain.auth.GetCurrentUser
import nz.co.chrisdrake.receipts.domain.image.GetPictureFile
import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.util.isUnmeteredNetwork

class PerformSync(
    private val getCurrentUser: GetCurrentUser,
    private val getPictureFile: GetPictureFile,
    private val remoteDataSource: RemoteDataSource,
    private val receiptRepository: ReceiptRepository,
    private val connectivityManager: ConnectivityManager,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex()

    operator fun invoke() = scope.launch {
        mutex.withLock {
            val userId = getCurrentUser()?.id ?: return@launch

            // TODO: Start a foreground notification

            try {
                remoteDataSource
                    .getReceipts(userId = userId)
                    .forEach { receipt ->
                        if (isActive && connectivityManager.isUnmeteredNetwork()) {
                            receiptRepository.getReceipt(id = receipt.id) ?: import(receipt)
                        }
                    }
            } catch (cancellation: Exception) {
                throw cancellation
            } catch (exception: Exception) {
                Firebase.crashlytics.recordException(exception)
            }
        }
    }

    private suspend fun import(receipt: Receipt) {
        val pictureFile = getPictureFile(receiptId = receipt.id)

        remoteDataSource.getImage(receipt.imageUri.toString(), pictureFile)

        receiptRepository.saveReceipt(
            receipt.copy(imageUri = pictureFile.toUri())
        )
    }
}