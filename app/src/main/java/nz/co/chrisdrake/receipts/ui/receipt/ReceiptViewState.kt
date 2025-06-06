package nz.co.chrisdrake.receipts.ui.receipt

import android.net.Uri
import nz.co.chrisdrake.receipts.ui.common.DateFieldState
import nz.co.chrisdrake.receipts.ui.common.InputFieldState
import nz.co.chrisdrake.receipts.ui.common.TimeFieldState

data class ReceiptViewState(
    val uri: Uri,
    val onPictureResult: (saved: Boolean) -> Unit,
    val details: Details? = null,
    val loading: Boolean = false,
    val dismissed: Boolean = false,
) {

    data class Details(
        val uri: Uri,
        val merchant: InputFieldState,
        val date: DateFieldState,
        val time: TimeFieldState,
        val items: List<Item>,
        val itemsError: String? = null,
        val onClickAddItem: () -> Unit,
        val onClickSave: () -> Unit,
    )

    data class Item(
        val id: String,
        val name: InputFieldState,
        val amount: InputFieldState,
        val onClickDelete: () -> Unit,
    )
}
