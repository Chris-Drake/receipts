package nz.co.chrisdrake.receipts.domain

import android.net.ConnectivityManager
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
import nz.co.chrisdrake.receipts.domain.auth.GetCurrentUser
import nz.co.chrisdrake.receipts.domain.model.BackupStatus.Failed
import nz.co.chrisdrake.receipts.domain.model.BackupStatus.InProgress
import nz.co.chrisdrake.receipts.domain.model.BackupStatus.NotStarted
import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.util.isUnmeteredNetwork
import kotlin.coroutines.cancellation.CancellationException

class BackupReceiptsAsync(
    private val getCurrentUser: GetCurrentUser,
    private val receiptRepository: ReceiptRepository,
    private val connectivityManager: ConnectivityManager,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex()
    private var firstRun = true

    operator fun invoke(receipts: List<Receipt>) = scope.launch {
        mutex.withLock {
            val currentUser = getCurrentUser() ?: return@launch

            if (firstRun) {
                resetIncompleteBackupStatus(receipts)
            }

            receipts.forEach { receipt ->
                if (isActive && connectivityManager.isUnmeteredNetwork()) {
                    try {
                        receiptRepository.backupReceipt(userId = currentUser.id, id = receipt.id)
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (exception: Exception) {
                        Firebase.crashlytics.recordException(exception)
                    }
                }
            }

            firstRun = false
        }
    }

    private suspend fun resetIncompleteBackupStatus(receipts: List<Receipt>) {
        receipts
            .filter { it.backUpStatus == InProgress || it.backUpStatus == Failed }
            .map { it.copy(backUpStatus = NotStarted) }
            .let { receiptRepository.updateReceipts(it) }
    }
}
