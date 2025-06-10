package nz.co.chrisdrake.receipts.ui.home

import nz.co.chrisdrake.receipts.ui.home.search.SearchBarState

data class HomeViewState(
    val receipts: List<ReceiptListItem> = emptyList(),
    val searchBar: SearchBarState,
    val onClickSearch: () -> Unit,
)
