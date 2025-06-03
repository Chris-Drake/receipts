package nz.co.chrisdrake.receipts.domain

import android.net.Uri
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

data class Receipt(
    val id: String,
    val uri: Uri,
    val merchant: String,
    val date: LocalDate,
    val time: LocalTime?,
    val items: List<ReceiptItem>,
    val createdAt: Long,
    val updatedAt: Long,
) {
    init {
        check(items.isNotEmpty()) { "Receipt must have at least one item" }
    }
}

data class ReceiptItem(
    val id: String,
    val name: String,
    val amount: BigDecimal,
)