package nz.co.chrisdrake.receipts.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nz.co.chrisdrake.receipts.DependencyRegistry.get
import nz.co.chrisdrake.receipts.domain.GetReceipts
import nz.co.chrisdrake.receipts.domain.PerformSync
import nz.co.chrisdrake.receipts.domain.SearchReceipts
import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.ui.common.DATE_FORMATTER
import nz.co.chrisdrake.receipts.ui.common.TIME_FORMATTER
import nz.co.chrisdrake.receipts.ui.home.search.SearchBarState

class HomeViewModel(
    private val getReceipts: GetReceipts = get(),
    private val searchReceipts: SearchReceipts = get(),
    performSync: PerformSync = get(),
) : ViewModel() {

    private var receipts: List<Receipt> = emptyList()

    private val _viewState = MutableStateFlow(
        HomeViewState(
            searchBar = SearchBarState(
                onQueryChange = ::onSearchQueryChange,
                onExpandedChange = ::onSearchBarExpandedChange,
            ),
            onClickSearch = { onSearchBarExpandedChange(true) },
        )
    )

    val viewState: StateFlow<HomeViewState> = _viewState

    init {
        loadReceipts()
        performSync()
    }

    private fun loadReceipts() = viewModelScope.launch {
        getReceipts().collect { receipts ->
            this@HomeViewModel.receipts = receipts

            val receiptListItems = receipts.map { it.toListItem() }

            _viewState.update { it.copy(receipts = receiptListItems) }
        }
    }

    private fun onSearchQueryChange(query: String) {
        _viewState.update {
            it.copy(
                searchBar = it.searchBar.copy(
                    query = query,
                    results = searchReceipts(receipts = receipts, query = query)
                        .map { receipt -> receipt.toListItem() },
                )
            )
        }
    }

    private fun onSearchBarExpandedChange(expanded: Boolean) {
        _viewState.update {
            it.copy(searchBar = it.searchBar.copy(expanded = expanded))
        }
    }

    private fun Receipt.toListItem(): ReceiptListItem {
        val date = date.format(DATE_FORMATTER)
        val time = time?.format(TIME_FORMATTER)

        return ReceiptListItem(
            id = id,
            imageUri = imageUri.toString(),
            merchant = merchant,
            dateTime = time?.let { "$date at $time" } ?: date,
            itemCount = "${items.size} item${if (items.size == 1) "" else "s"}",
            totalAmount = "$${totalAmount}",
            backupStatus = backUpStatus,
        )
    }
}
