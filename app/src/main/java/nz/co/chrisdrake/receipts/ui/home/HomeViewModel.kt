package nz.co.chrisdrake.receipts.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nz.co.chrisdrake.receipts.DependencyRegistry.get
import nz.co.chrisdrake.receipts.domain.GetReceipts
import nz.co.chrisdrake.receipts.domain.Receipt
import nz.co.chrisdrake.receipts.ui.common.DATE_FORMATTER
import nz.co.chrisdrake.receipts.ui.common.TIME_FORMATTER

class HomeViewModel(
    private val getReceipts: GetReceipts = get(),
) : ViewModel() {

    private val _viewState = MutableStateFlow(HomeViewState())

    val viewState: StateFlow<HomeViewState> = _viewState

    init {
        loadReceipts()
    }

    private fun loadReceipts() = viewModelScope.launch {
        getReceipts().collect { receipts ->
            val receiptListItems = receipts.map { it.toListItem() }

            _viewState.update { it.copy(receipts = receiptListItems) }
        }
    }

    private fun Receipt.toListItem() = ReceiptListItem(
        id = id,
        imageUri = imageUri.toString(),
        merchant = merchant,
        date = date.format(DATE_FORMATTER),
        time = time?.format(TIME_FORMATTER),
        itemCount = items.size,
        totalAmount = "$${totalAmount}"
    )
}
