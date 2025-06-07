package nz.co.chrisdrake.receipts.ui.receipt

import android.net.Uri
import nz.co.chrisdrake.receipts.ui.common.DateFieldState
import nz.co.chrisdrake.receipts.ui.common.InputFieldState
import nz.co.chrisdrake.receipts.ui.common.TimeFieldState
import java.math.BigDecimal
import java.math.RoundingMode

data class ReceiptViewState(
    val title: String,
    val createTempImageUri: () -> Uri,
    val onPictureResult: (uri: Uri, saved: Boolean) -> Unit,
    val details: Details? = null,
    val loadingMessage: String? = null,
    val deleteVisible: Boolean,
    val onClickDelete: () -> Unit,
    val dismissed: Boolean = false,
) {

    data class Details(
        val imageUri: Uri,
        val merchant: InputFieldState,
        val date: DateFieldState,
        val time: TimeFieldState,
        val items: List<Item>,
        val editing: Boolean,
        val itemsError: String? = null,
        val onClickAddItem: () -> Unit,
        val onClickSave: () -> Unit,
        val onClickEdit: () -> Unit,
    ) {
        val formattedDateTime = time.formattedValue.takeIf { it.isNotBlank() }
            ?.let { "${date.formattedValue} at $it" }
            ?: date.formattedValue

        val formattedTotal: String? = if (items.size > 1) {
            "$" + items.sumOf { it.amount.value.toBigDecimalOrNull() ?: BigDecimal.ZERO }.setScale(2, RoundingMode.HALF_UP)
        } else {
            null
        }
    }

    data class Item(
        val id: String,
        val name: InputFieldState,
        val amount: InputFieldState,
        val onClickDelete: () -> Unit,
    )
}
