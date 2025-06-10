package nz.co.chrisdrake.receipts.ui.home.search

import nz.co.chrisdrake.receipts.ui.home.ReceiptListItem

data class SearchBarState(
    val query: String = "",
    val onQueryChange: (String) -> Unit,
    val expanded: Boolean = false,
    val onExpandedChange: (Boolean) -> Unit,
    val results: List<ReceiptListItem> = emptyList(),
)