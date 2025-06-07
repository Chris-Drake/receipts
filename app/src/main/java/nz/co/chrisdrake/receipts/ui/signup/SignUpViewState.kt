package nz.co.chrisdrake.receipts.ui.signup

import nz.co.chrisdrake.receipts.ui.common.InputFieldState

data class SignUpViewState(
    val loading: Boolean = false,
    val complete: Boolean = false,
    val email: InputFieldState,
    val password: InputFieldState,
    val confirmPassword: InputFieldState,
    val onClickSignUp: () -> Unit,
    val errorMessage: String? = null,
)