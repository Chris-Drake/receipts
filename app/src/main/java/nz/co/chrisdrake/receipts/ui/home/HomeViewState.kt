package nz.co.chrisdrake.receipts.ui.home

data class HomeViewState(
    val receipts: List<ReceiptListItem> = emptyList(),
)
