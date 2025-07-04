package nz.co.chrisdrake.receipts.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.co.chrisdrake.receipts.domain.model.BackupStatus
import nz.co.chrisdrake.receipts.domain.model.BackupStatus.Completed
import nz.co.chrisdrake.receipts.domain.model.BackupStatus.Failed
import nz.co.chrisdrake.receipts.domain.model.BackupStatus.InProgress
import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.domain.model.ReceiptId

class ReceiptRepository(
    private val dao: ReceiptDao,
    private val remoteDataSource: RemoteDataSource,
) {

    fun getReceipts(): Flow<List<Receipt>> {
        return dao.getReceipts().map {
            it.map(ReceiptWithItemsEntity::toDomain)
        }
    }

    suspend fun getReceipt(id: ReceiptId): Receipt? {
        return dao.getReceipt(id = id)?.toDomain()
    }

    suspend fun saveReceipt(receipt: Receipt) {
        val (receiptEntity, receiptItems) = receipt.toEntity()

        dao.insertReceiptWithItems(receiptEntity, receiptItems)
    }

    suspend fun backupReceipt(userId: String, id: ReceiptId) {
        val receipt = getReceipt(id) ?: return

        if (receipt.backUpStatus == Completed
            || receipt.backUpStatus == InProgress
            || receipt.backUpStatus == Failed) {
            return
        }

        suspend fun setBackupStatus(status: BackupStatus) {
            updateReceipt(receipt = receipt.copy(backUpStatus = status))
        }

        setBackupStatus(InProgress)

        try {
            val downloadPaths = remoteDataSource.uploadImages(userId = userId, receipt = receipt)

            remoteDataSource.saveReceipt(
                userId = userId,
                receipt = receipt,
                downloadPaths = downloadPaths,
            )

            updateReceipt(
                receipt = receipt.copy(
                    imageDownloadPaths = downloadPaths,
                    backUpStatus = Completed,
                )
            )
        } catch (exception: Exception) {
            setBackupStatus(Failed)
            throw exception
        }
    }

    suspend fun updateReceipt(receipt: Receipt) {
        val (receiptEntity, receiptItems) = receipt.toEntity()

        dao.updateReceiptWithItems(receiptEntity, receiptItems)
    }

    suspend fun updateReceipts(receipts: List<Receipt>) {
        dao.updateReceipts(receipts.map(Receipt::toEntity))
    }

    suspend fun deleteReceipt(userId: String?, id: ReceiptId) {
        userId?.let { remoteDataSource.deleteReceipt(userId = userId, id = id)}
        dao.deleteReceipt(id = id)
    }
}