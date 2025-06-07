package nz.co.chrisdrake.receipts.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import nz.co.chrisdrake.receipts.domain.ReceiptId

@Dao
interface ReceiptDao {

    @Transaction
    @Query("SELECT * FROM receipts ORDER BY receipt_date DESC, receipt_time DESC")
    fun getReceipts(): Flow<List<ReceiptWithItemsEntity>>

    @Transaction
    @Query("SELECT * FROM receipts WHERE receipt_id = :id")
    suspend fun getReceipt(id: ReceiptId): ReceiptWithItemsEntity

    @Insert
    suspend fun insertReceipt(receipt: ReceiptEntity)

    @Insert
    suspend fun insertReceiptItems(items: List<ReceiptItemEntity>)

    @Transaction
    suspend fun insertReceiptWithItems(receipt: ReceiptEntity, items: List<ReceiptItemEntity>) {
        insertReceipt(receipt)
        insertReceiptItems(items)
    }

    @Update
    suspend fun updateReceipt(receipt: ReceiptEntity)

    @Transaction
    suspend fun updateReceiptWithItems(receipt: ReceiptEntity, items: List<ReceiptItemEntity>) {
        updateReceipt(receipt)
        deleteReceiptItems(receipt.id)
        insertReceiptItems(items)
    }

    @Query("DELETE FROM receipts WHERE receipt_id = :id")
    suspend fun deleteReceipt(id: ReceiptId)

    @Query("DELETE FROM receipt_items WHERE receipt_item_receipt_id = :id")
    suspend fun deleteReceiptItems(id: ReceiptId)
}