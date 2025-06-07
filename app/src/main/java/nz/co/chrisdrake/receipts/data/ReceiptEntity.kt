package nz.co.chrisdrake.receipts.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import com.google.firebase.firestore.PropertyName
import nz.co.chrisdrake.receipts.domain.BackupStatus
import nz.co.chrisdrake.receipts.domain.ReceiptId
import nz.co.chrisdrake.receipts.domain.ReceiptItemId
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "receipts")
data class ReceiptEntity(
    @ColumnInfo(name = "receipt_id") @PrimaryKey val id: ReceiptId,
    @ColumnInfo(name = "receipt_image_uri") val imageUri: String,
    @ColumnInfo(name = "receipt_merchant") val merchant: String,
    @ColumnInfo(name = "receipt_date") val date: LocalDate,
    @ColumnInfo(name = "receipt_time") val time: LocalTime?,
    @ColumnInfo(name = "receipt_backup_status") val backupStatus: BackupStatus,
    @ColumnInfo(name = "receipt_created_at") val createdAt: Long,
    @ColumnInfo(name = "receipt_updated_at") val updatedAt: Long,
)

@Entity(
    tableName = "receipt_items",
    foreignKeys = [
        ForeignKey(
            entity = ReceiptEntity::class,
            parentColumns = ["receipt_id"],
            childColumns = ["receipt_item_receipt_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("receipt_item_receipt_id")]
)
data class ReceiptItemEntity(
    @PrimaryKey @ColumnInfo(name = "receipt_item_id") val id: ReceiptItemId,
    @ColumnInfo(name = "receipt_item_receipt_id") val receiptId: ReceiptId,
    @ColumnInfo(name = "receipt_item_name") val name: String,
    @ColumnInfo(name = "receipt_item_amount") val amount: BigDecimal,
)

data class ReceiptWithItemsEntity(
    @Embedded
    val receipt: ReceiptEntity,
    @Relation(parentColumn = "receipt_id", entityColumn = "receipt_item_receipt_id")
    val items: List<ReceiptItemEntity>,
)

data class RemoteReceiptEntity(
    @PropertyName("id") val id: String,
    @PropertyName("image_path") val imagePath: String,
    @PropertyName("merchant") val merchant: String,
    @PropertyName("date") val date: String,
    @PropertyName("time") val time: String?,
    @PropertyName("items") val items: List<RemoteReceiptItemEntity>,
    @PropertyName("created_at") val createdAt: Long,
    @PropertyName("updated_at") val updatedAt: Long,
)

data class RemoteReceiptItemEntity(
    @PropertyName("id") val id: String,
    @PropertyName("name") val name: String,
    @PropertyName("amount") val amount: String,
)