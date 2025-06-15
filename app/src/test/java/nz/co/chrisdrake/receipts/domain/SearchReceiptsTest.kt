package nz.co.chrisdrake.receipts.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

class SearchReceiptsTest {

    private val searchReceipts = SearchReceipts()

    @Test
    fun `returns empty list for blank query`() {
        val receipts = listOf(createReceipt())

        val result = searchReceipts(receipts, "   ")

        assertThat(result).isEmpty()
    }

    @Test
    fun `matches merchant name ignoring case`() {
        val receipts = listOf(createReceipt(merchant = "SuperMart"))

        val result = searchReceipts(receipts, "supermart")

        assertThat(result).hasSize(1)
    }

    @Test
    fun `matches date in yyyy-MM-dd format`() {
        val date = LocalDate.of(2025, Month.JUNE, 15)
        val receipts = listOf(createReceipt(date = date))

        val result = searchReceipts(receipts, "2025-06-15")

        assertThat(result).hasSize(1)
    }

    @Test
    fun `matches date in dd-MM-yyyy format`() {
        val date = LocalDate.of(2025, Month.JUNE, 15)
        val receipts = listOf(createReceipt(date = date))

        val result = searchReceipts(receipts, "15-06-2025")

        assertThat(result).hasSize(1)
    }

    @Test
    fun `matches item name ignoring case`() {
        val item = createItem(name = "Milk", amount = BigDecimal(2.0))
        val receipts = listOf(createReceipt(items = listOf(item)))

        val result = searchReceipts(receipts, "milk")

        assertThat(result).hasSize(1)
    }

    @Test
    fun `matches item amount`() {
        val item = createItem(amount = BigDecimal(3.5))
        val receipts = listOf(createReceipt(items = listOf(item)))

        val result = searchReceipts(receipts, "3.5")

        assertThat(result).hasSize(1)
    }
}
