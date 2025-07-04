package nz.co.chrisdrake.receipts.domain.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime

typealias ReceiptId = String
typealias ReceiptItemId = String

data class Receipt(
    val id: ReceiptId,
    val imageFilePaths: ReceiptImageFilePaths?,
    val imageDownloadPaths: ReceiptImageDownloadPaths?,
    val merchant: String,
    val date: LocalDate,
    val time: LocalTime?,
    val items: List<ReceiptItem>,
    val backUpStatus: BackupStatus,
    val createdAt: Long,
    val updatedAt: Long,
    val accessedAt: Long?,
) {
    val totalAmount: BigDecimal = items.sumOf { it.amount }.setScale(2, RoundingMode.HALF_UP)

    init {
        check(items.isNotEmpty()) { "Receipt must have at least one item" }
    }
}

data class ReceiptImageFilePaths(
    val original: String?,
    val thumbnail: String,
)

data class ReceiptImageDownloadPaths(
    val original: String,
    val thumbnail: String,
)

data class ReceiptItem(
    val id: ReceiptItemId,
    val name: String,
    val amount: BigDecimal,
)