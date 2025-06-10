package nz.co.chrisdrake.receipts.domain

import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.domain.model.ReceiptItem
import java.time.format.DateTimeFormatter

class SearchReceipts {

    private val searchDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val searchDateFormatReversed = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    operator fun invoke(receipts: List<Receipt>, query: String): List<Receipt> {
        val trimmedQuery = query.trim().takeUnless { it.isEmpty() }
            ?: return emptyList()

        return receipts.filter { receipt -> receipt.matches(trimmedQuery) }
    }

    private fun Receipt.matches(query: String): Boolean {
        return merchant.contains(query, ignoreCase = true)
                || date.format(searchDateFormat).contains(query)
                || date.format(searchDateFormatReversed).contains(query)
                || items.any { item -> item.matches(query) }
    }

    private fun ReceiptItem.matches(query: String): Boolean {
        return name.contains(query, ignoreCase = true) || amount.toString().contains(query)
    }
}