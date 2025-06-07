package nz.co.chrisdrake.receipts.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.co.chrisdrake.receipts.domain.BackupStatus
import nz.co.chrisdrake.receipts.domain.BackupStatus.Completed
import nz.co.chrisdrake.receipts.domain.BackupStatus.Failed
import nz.co.chrisdrake.receipts.domain.BackupStatus.InProgress
import nz.co.chrisdrake.receipts.domain.Receipt
import nz.co.chrisdrake.receipts.domain.ReceiptId

class ReceiptRepository(
    private val dao: ReceiptDao,
    private val remoteDataSource: RemoteDataSource,
) {

    fun getReceipts(): Flow<List<Receipt>> {
        return dao.getReceipts().map {
            it.map(ReceiptWithItemsEntity::toDomain)
        }
    }

    suspend fun getReceipt(id: ReceiptId): Receipt {
        return dao.getReceipt(id = id).toDomain()
    }

    suspend fun saveReceipt(receipt: Receipt) {
        val (receiptEntity, receiptItems) = receipt.toEntity()

        dao.insertReceiptWithItems(receiptEntity, receiptItems)
    }

    suspend fun backupReceipt(userId: String, receipt: Receipt) {
        if (receipt.backUpStatus == Completed || receipt.backUpStatus == InProgress) {
            return
        }

        suspend fun setBackupStatus(status: BackupStatus) {
            updateReceipt(receipt = receipt.copy(backUpStatus = status))
        }

        setBackupStatus(InProgress)

        try {
            remoteDataSource.saveReceipt(userId = userId, receipt = receipt)
        } catch (exception: Exception) {
            setBackupStatus(Failed)
            throw exception
        }

        setBackupStatus(Completed)
    }

    suspend fun updateReceipt(receipt: Receipt) {
        val (receiptEntity, receiptItems) = receipt.toEntity()

        dao.updateReceiptWithItems(receiptEntity, receiptItems)
    }

    suspend fun deleteReceipt(userId: String?, id: ReceiptId) {
        userId?.let { remoteDataSource.deleteReceipt(userId = userId, id = id)}
        dao.deleteReceipt(id = id)
    }
}