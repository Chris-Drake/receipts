package nz.co.chrisdrake.receipts.ui.receipt

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import nz.co.chrisdrake.receipts.domain.GetTempImageUri
import nz.co.chrisdrake.receipts.domain.Receipt
import nz.co.chrisdrake.receipts.domain.ReceiptItem
import nz.co.chrisdrake.receipts.ui.common.DateFieldState
import nz.co.chrisdrake.receipts.ui.common.InputFieldState
import nz.co.chrisdrake.receipts.ui.common.TimeFieldState
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptViewState.Details
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptViewState.Item
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class ReceiptViewModel(
    getTempImageUri: GetTempImageUri,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        ReceiptViewState(
            uri = getTempImageUri(),
            onPictureResult = ::onPictureResult,
        )
    )

    val viewState: StateFlow<ReceiptViewState> = _viewState

    private fun onPictureResult(saved: Boolean) {
        if (!saved) {
            if (viewState.value.details == null) {
                _viewState.update { it.copy(dismissed = true) }
            }

            return
        }

        _viewState.update {
            it.copy(
                details = Details(
                    uri = it.uri,
                    merchant = InputFieldState(
                        label = "Merchant",
                        onValueChanged = ::onMerchantChanged
                    ),
                    date = DateFieldState(onDateSelected = ::onDateSelected),
                    time = TimeFieldState(onTimeSelected = ::onTimeSelected),
                    items = listOf(createItem()),
                    onClickSave = ::onClickSave,
                    onClickAddItem = ::onClickAddItem,
                )
            )
        }
    }

    private fun onClickSave() {
        validateFields()

        val details = checkNotNull(viewState.value.details)
        val merchant = details.merchant.sanitizedValue ?: return
        val date = details.date.selection ?: return
        val time = details.time.selection

        val items = details.items
            .mapNotNull {
                ReceiptItem(
                    id = it.id,
                    name = it.name.sanitizedValue ?: return@mapNotNull null,
                    amount = it.amount.sanitizedValue?.toBigDecimalOrNull() ?: return@mapNotNull null,
                )
            }
            .takeIf { it.isNotEmpty() }
            ?: return

        val receipt = Receipt(
            id = UUID.randomUUID().toString(),
            uri = details.uri,
            merchant = merchant,
            date = date,
            time = time,
            items = items,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )

        // TODO: Save

        _viewState.update { it.copy(dismissed = true) }
    }

    private fun validateFields() {
        updateDetails {
            with(it) {
                copy(
                    merchant = merchant.copy(error = errorIfNull(merchant.sanitizedValue)),
                    date = date.copy(error = errorIfNull(date.selection)),
                    items = items.map(::withValidation),
                    itemsError = errorIf(items.isEmpty()),
                )
            }
        }
    }

    private fun withValidation(item: Item): Item = with(item) {
        copy(
            name = name.copy(error = errorIfNull(name.sanitizedValue)),
            amount = amount.copy(error = errorIfNull(amount.sanitizedValue)),
        )
    }

    private val InputFieldState.sanitizedValue: String?
        get() = value.trim().takeUnless(String::isEmpty)

    private fun createItem(): Item {
        val id = UUID.randomUUID().toString()

        return Item(
            id = id,
            name = InputFieldState(
                label = "Item",
                onValueChanged = { value ->
                    updateItem(id) { it.copy(name = it.name.copy(value = value, error = null)) }
                }
            ),
            amount = InputFieldState(
                label = "Amount",
                onValueChanged = { value ->
                    if (value.isNotEmpty() && value.toBigDecimalOrNull() == null) {
                        return@InputFieldState
                    }

                    updateItem(id) { it.copy(amount = it.amount.copy(value = value, error = null)) }
                }
            ),
            onClickDelete = { onClickClearItem(id) },
        )
    }

    private fun onClickAddItem() {
        updateDetails { details ->
            details.copy(items = details.items + createItem(), itemsError = null)
        }
    }

    private fun onClickClearItem(id: Any) {
        updateDetails { details ->
            details.copy(items = details.items.filterNot { it.id == id }, itemsError = null)
        }
    }

    private fun onMerchantChanged(value: String) {
        updateDetails { it.copy(merchant = it.merchant.copy(value = value, error = null)) }
    }

    private fun onDateSelected(date: LocalDate) {
        updateDetails { details ->
            details.copy(date = details.date.copy(selection = date, error = null))
        }
    }

    private fun onTimeSelected(time: LocalTime) {
        updateDetails { details ->
            details.copy(time = details.time.copy(selection = time))
        }
    }

    private fun updateItem(id: Any, function: (Item) -> Item) {
        updateDetails { details ->
            details.copy(
                items = details.items.map { item ->
                    if (item.id == id) function(item) else item
                },
                itemsError = null,
            )
        }
    }

    private fun updateDetails(function: (Details) -> Details) {
        _viewState.update { it.copy(details = it.details?.let(function)) }
    }

    private fun errorIfNull(value: Any?): String? = errorIf(value == null)

    private fun errorIf(condition: Boolean, errorMessage: String = "Required"): String? =
        if (condition) errorMessage else null
}
