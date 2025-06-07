package nz.co.chrisdrake.receipts.data

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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

@Keep
data class RemoteReceiptEntity(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("image_path") @set:PropertyName("image_path") var imagePath: String = "",
    @get:PropertyName("merchant") @set:PropertyName("merchant") var merchant: String = "",
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("time") @set:PropertyName("time") var time: String? = null,
    @get:PropertyName("items") @set:PropertyName("items") var items: List<RemoteReceiptItemEntity> = emptyList(),
    @get:PropertyName("created_at") @set:PropertyName("created_at") var createdAt: Long = 0,
    @get:PropertyName("updated_at") @set:PropertyName("updated_at") var updatedAt: Long = 0,
)

@Keep
data class RemoteReceiptItemEntity(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("amount") @set:PropertyName("amount") var amount: String = "",
)