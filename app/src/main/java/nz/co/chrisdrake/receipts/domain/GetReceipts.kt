package nz.co.chrisdrake.receipts.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import nz.co.chrisdrake.receipts.DependencyRegistry.get
import nz.co.chrisdrake.receipts.data.ReceiptRepository

class GetReceipts(
    private val repository: ReceiptRepository,
    private val backupReceiptsAsync: BackupReceiptsAsync = get(),
) {
    operator fun invoke(): Flow<List<Receipt>> {
        return repository.getReceipts()
            .onEach { backupReceiptsAsync(it) }
    }
}
