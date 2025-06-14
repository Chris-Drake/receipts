package nz.co.chrisdrake.receipts.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nz.co.chrisdrake.receipts.DependencyRegistry.get
import nz.co.chrisdrake.receipts.R
import nz.co.chrisdrake.receipts.domain.auth.SignUp
import nz.co.chrisdrake.receipts.ui.common.InputFieldState
import nz.co.chrisdrake.receipts.util.ResourceProvider
import kotlin.coroutines.cancellation.CancellationException

class SignUpViewModel(
    private val signUp: SignUp = get(),
    private val resourceProvider: ResourceProvider = get(),
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SignUpViewState(
            email = InputFieldState(
                label = resourceProvider.getString(R.string.common_email_label),
                onValueChanged = ::onEmailChanged
            ),
            password = InputFieldState(
                label = resourceProvider.getString(R.string.common_password_label),
                onValueChanged = ::onPasswordChanged
            ),
            confirmPassword = InputFieldState(
                label = resourceProvider.getString(R.string.sign_up_password_confirm_label),
                onValueChanged = ::onConfirmPasswordChanged
            ),
            onClickSignUp = ::attemptSignUp,
        )
    )

    val viewState: StateFlow<SignUpViewState> = _viewState

    private fun onEmailChanged(email: String) {
        _viewState.update {
            it.copy(
                email = it.email.copy(value = email, error = null),
                errorMessage = null,
            )
        }
    }

    private fun onPasswordChanged(password: String) {
        _viewState.update {
            it.copy(
                password = it.password.copy(value = password, error = null),
                errorMessage = null,
            )
        }
    }

    private fun onConfirmPasswordChanged(confirmPassword: String) {
        _viewState.update {
            it.copy(
                confirmPassword = it.confirmPassword.copy(value = confirmPassword, error = null),
                errorMessage = null,
            )
        }
    }

    private fun attemptSignUp() {
        val currentState = _viewState.value
        val email = currentState.email.value.trim()
        val password = currentState.password.value
        val confirmPassword = currentState.confirmPassword.value

        if (email.isEmpty()) {
            _viewState.update { it.copy(email = it.email.copy(error = resourceProvider.getString(R.string.common_input_field_required))) }
            return
        }

        if (password.isEmpty()) {
            _viewState.update {
                it.copy(password = it.password.copy(error = resourceProvider.getString(R.string.common_input_field_required)))
            }
            return
        }

        if (confirmPassword.isEmpty()) {
            _viewState.update {
                it.copy(confirmPassword = it.confirmPassword.copy(error = resourceProvider.getString(R.string.common_input_field_required)))
            }
            return
        }

        if (password != confirmPassword) {
            _viewState.update {
                it.copy(confirmPassword = it.confirmPassword.copy(error = resourceProvider.getString(R.string.sign_up_passwords_do_not_match)))
            }
            return
        }

        _viewState.update { it.copy(loading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                signUp(email, password)

                _viewState.update { it.copy(complete = true) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                _viewState.update { it.copy(loading = false, errorMessage = exception.message) }
            }
        }
    }
}
