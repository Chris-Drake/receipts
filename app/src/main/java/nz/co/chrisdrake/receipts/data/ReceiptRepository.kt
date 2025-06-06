package nz.co.chrisdrake.receipts.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.co.chrisdrake.receipts.domain.Receipt
import nz.co.chrisdrake.receipts.domain.ReceiptId

class ReceiptRepository(
    private val dao: ReceiptDao,
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

    suspend fun updateReceipt(receipt: Receipt) {
        val (receiptEntity, receiptItems) = receipt.toEntity()

        dao.updateReceiptWithItems(receiptEntity, receiptItems)
    }

    suspend fun deleteReceipt(id: ReceiptId) {
        dao.deleteReceipt(id = id)
    }
}