package nz.co.chrisdrake.receipts.domain

import android.net.Uri
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime

typealias ReceiptId = String
typealias ReceiptItemId = String

data class Receipt(
    val id: ReceiptId,
    val imageUri: Uri,
    val merchant: String,
    val date: LocalDate,
    val time: LocalTime?,
    val items: List<ReceiptItem>,
    val backUpStatus: BackupStatus,
    val createdAt: Long,
    val updatedAt: Long,
) {
    val totalAmount: BigDecimal = items.sumOf { it.amount }.setScale(2, RoundingMode.HALF_UP)

    init {
        check(items.isNotEmpty()) { "Receipt must have at least one item" }
    }
}

data class ReceiptItem(
    val id: ReceiptItemId,
    val name: String,
    val amount: BigDecimal,
)