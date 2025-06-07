package nz.co.chrisdrake.receipts.ui.signin

import nz.co.chrisdrake.receipts.ui.common.InputFieldState

data class SignInViewState(
    val loading: Boolean = false,
    val complete: Boolean = false,
    val email: InputFieldState,
    val password: InputFieldState,
    val onClickSignIn: () -> Unit,
    val errorMessage: String? = null,
)
