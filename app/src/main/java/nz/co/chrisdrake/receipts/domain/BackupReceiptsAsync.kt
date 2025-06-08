package nz.co.chrisdrake.receipts.domain

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
import nz.co.chrisdrake.receipts.domain.model.Receipt
import kotlin.coroutines.cancellation.CancellationException

class BackupReceiptsAsync(
    private val getCurrentUser: GetCurrentUser,
    private val receiptRepository: ReceiptRepository,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex()

    operator fun invoke(receipts: List<Receipt>) = scope.launch {
        mutex.withLock {
            val currentUser = getCurrentUser() ?: return@launch

            receipts.forEach { receipt ->
                if (isActive) {
                    try {
                        receiptRepository.backupReceipt(userId = currentUser.id, receipt = receipt)
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (exception: Exception) {
                        Firebase.crashlytics.recordException(exception)
                    }
                }
            }
        }
    }
}