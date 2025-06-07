package nz.co.chrisdrake.receipts.ui.profile

data class ProfileViewState(
    val email: String,
    val loading: Boolean = false,
    val complete: Boolean = false,
    val onClickSignOut: () -> Unit,
    val errorMessage: String? = null,
)