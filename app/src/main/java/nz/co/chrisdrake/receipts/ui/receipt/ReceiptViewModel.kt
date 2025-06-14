package nz.co.chrisdrake.receipts.ui.receipt

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nz.co.chrisdrake.receipts.DependencyRegistry.get
import nz.co.chrisdrake.receipts.R
import nz.co.chrisdrake.receipts.domain.DeleteReceipt
import nz.co.chrisdrake.receipts.domain.GetOriginalImageUri
import nz.co.chrisdrake.receipts.domain.GetReceipt
import nz.co.chrisdrake.receipts.domain.SaveReceipt
import nz.co.chrisdrake.receipts.domain.UpdateReceipt
import nz.co.chrisdrake.receipts.domain.image.CopyImagesToInternalStorage
import nz.co.chrisdrake.receipts.domain.image.GetTempImageUri
import nz.co.chrisdrake.receipts.domain.image.OpenImage
import nz.co.chrisdrake.receipts.domain.image.ScanImage
import nz.co.chrisdrake.receipts.domain.model.BackupStatus
import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.domain.model.ReceiptId
import nz.co.chrisdrake.receipts.domain.model.ReceiptItem
import nz.co.chrisdrake.receipts.ui.common.DateFieldState
import nz.co.chrisdrake.receipts.ui.common.InputFieldState
import nz.co.chrisdrake.receipts.ui.common.TimeFieldState
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptViewState.Details
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptViewState.Item
import nz.co.chrisdrake.receipts.util.ResourceProvider
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

class ReceiptViewModel(
    private val existingId: ReceiptId?,
    getTempImageUri: GetTempImageUri = get(),
    private val openImage: OpenImage = get(),
    private val copyImagesToInternalStorage: CopyImagesToInternalStorage = get(),
    private val getOriginalImageUri: GetOriginalImageUri = get(),
    private val scanImage: ScanImage = get(),
    private val getReceipt: GetReceipt = get(),
    private val saveReceipt: SaveReceipt = get(),
    private val updateReceipt: UpdateReceipt = get(),
    private val deleteReceipt: DeleteReceipt = get(),
    private val resourceProvider: ResourceProvider = get(),
) : ViewModel() {

    private var existingReceipt: Receipt? = null

    private val _viewState = MutableStateFlow(
        ReceiptViewState(
            title = if (existingId == null) {
                resourceProvider.getString(R.string.receipt_new_receipt_title)
            } else {
                resourceProvider.getString(R.string.receipt_title)
            },
            createTempImageUri = { getTempImageUri() },
            onPictureResult = ::onPictureResult,
            deleteVisible = existingId != null,
            onClickDelete = ::onClickDelete,
        )
    )

    val viewState: StateFlow<ReceiptViewState> = _viewState

    init {
        existingId?.let(::loadExistingReceipt)
    }

    private fun loadExistingReceipt(id: ReceiptId) {
        _viewState.update { it.copy(loadingMessage = resourceProvider.getString(R.string.receipt_loading_message)) }

        viewModelScope.launch {
            existingReceipt = getReceipt(id = id).also {
                initializeDetails(receipt = it, imageUri = getOriginalImageUri(it))
            }
        }
    }

    private fun onPictureResult(uri: Uri, saved: Boolean) {
        if (!saved) {
            if (viewState.value.details == null) {
                _viewState.update { it.copy(dismissed = true) }
            }

            return
        }

        if (existingReceipt == null) {
            initializeDetails(receipt = null, imageUri = uri)
            attemptScan(uri)
        } else {
            _viewState.update { it.copy(details = it.details?.copy(imageUri = uri)) }
        }
    }

    private fun initializeDetails(receipt: Receipt?, imageUri: Uri) {
        _viewState.update { currentState ->
            val editing = receipt == null

            currentState.copy(
                loadingMessage = null,
                details = Details(
                    imageUri = imageUri,
                    merchant = InputFieldState(
                        label = resourceProvider.getString(R.string.receipt_merchant_label),
                        value = receipt?.merchant ?: "",
                        onValueChanged = ::onMerchantChanged
                    ),
                    date = DateFieldState(
                        selection = receipt?.date,
                        onDateSelected = ::onDateSelected
                    ),
                    time = TimeFieldState(
                        selection = receipt?.time,
                        onTimeSelected = ::onTimeSelected
                    ),
                    items = receipt?.items?.map { createItem(from = it) } ?: listOf(createItem()),
                    editing = editing,
                    onClickSave = ::onClickSave,
                    onClickAddItem = ::onClickAddItem,
                    onClickEdit = ::onClickEdit,
                    onClickOpenImage = ::onClickOpenImage,
                )
            )
        }
    }

    private fun onClickOpenImage() {
        viewState.value.details?.imageUri?.let {
            openImage(it)
        }
    }

    private fun attemptScan(imageUri: Uri) = viewModelScope.launch {
        _viewState.update { it.copy(loadingMessage = resourceProvider.getString(R.string.receipt_scanning_message)) }

        try {
            val scanResult = scanImage(imageUri = imageUri)

            updateDetails {
                it.copy(
                    merchant = it.merchant.copy(value = scanResult.merchant ?: it.merchant.value),
                    date = it.date.copy(selection = scanResult.date ?: it.date.selection),
                    time = it.time.copy(selection = scanResult.time ?: it.time.selection),
                    items = scanResult.items?.map { item -> createItem(from = item) } ?: it.items,
                )
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            Firebase.crashlytics.recordException(exception)
            // TODO: Show error, allow retry
        }

        _viewState.update { it.copy(loadingMessage = null) }
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
            .takeIf { it.isNotEmpty() && it.size == details.items.size }
            ?: return

        viewModelScope.launch {
            _viewState.update { it.copy(loadingMessage = resourceProvider.getString(R.string.receipt_saving_message)) }

            val id = existingReceipt?.id ?: UUID.randomUUID().toString()
            val imageFilePaths = copyImagesToInternalStorage(uri = details.imageUri, receiptId = id)

            val receipt = Receipt(
                id = id,
                imageFilePaths = imageFilePaths,
                imageDownloadPaths = null,
                merchant = merchant,
                date = date,
                time = time,
                items = items,
                backUpStatus = BackupStatus.NotStarted,
                createdAt = existingReceipt?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                accessedAt = System.currentTimeMillis(),
            )

            try {
                if (existingReceipt == null) {
                    saveReceipt(receipt)
                    _viewState.update { it.copy(loadingMessage = null, dismissed = true) }
                } else {
                    updateReceipt(receipt)
                    _viewState.update {
                        it.copy(loadingMessage = null, details = it.details?.copy(editing = false))
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                TODO()
            }
        }
    }

    private fun onClickDelete() {
        _viewState.update { it.copy(loadingMessage = resourceProvider.getString(R.string.receipt_deleting_message)) }

        viewModelScope.launch {
            try {
                deleteReceipt(id = checkNotNull(existingId))
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                TODO()
            }

            _viewState.update { it.copy(loadingMessage = null, dismissed = true) }
        }
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
        get() = value.trim().removeSuffix(".").takeUnless(String::isEmpty)

    private fun createItem(from: ReceiptItem? = null): Item {
        val id = from?.id ?: UUID.randomUUID().toString()

        return Item(
            id = id,
            name = InputFieldState(
                label = resourceProvider.getString(R.string.receipt_item_label),
                value = from?.name ?: "",
                onValueChanged = { value ->
                    updateItem(id) { it.copy(name = it.name.copy(value = value, error = null)) }
                }
            ),
            amount = InputFieldState(
                label = resourceProvider.getString(R.string.receipt_amount_label),
                value = from?.amount?.toString() ?: "",
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

    private fun onClickEdit() {
        updateDetails {
            it.copy(editing = true)
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

    private fun errorIf(
        condition: Boolean,
        errorMessage: String = resourceProvider.getString(R.string.common_input_field_required),
    ): String? = if (condition) errorMessage else null
}
