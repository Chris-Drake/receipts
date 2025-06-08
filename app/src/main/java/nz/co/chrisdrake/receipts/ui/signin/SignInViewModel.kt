package nz.co.chrisdrake.receipts.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nz.co.chrisdrake.receipts.DependencyRegistry.get
import nz.co.chrisdrake.receipts.domain.auth.SignIn
import nz.co.chrisdrake.receipts.ui.common.InputFieldState
import kotlin.coroutines.cancellation.CancellationException

class SignInViewModel(
    private val signIn: SignIn = get(),
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SignInViewState(
            email = InputFieldState(
                label = "Email",
                onValueChanged = ::onEmailChanged
            ),
            password = InputFieldState(
                label = "Password",
                onValueChanged = ::onPasswordChanged
            ),
            onClickSignIn = ::attemptSignIn,
        )
    )

    val viewState: StateFlow<SignInViewState> = _viewState

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

    private fun attemptSignIn() {
        val currentState = _viewState.value
        val email = currentState.email.value.trim()
        val password = currentState.password.value

        if (email.isEmpty()) {
            _viewState.update { it.copy(email = it.email.copy(error = "Required")) }
            return
        }

        if (password.isEmpty()) {
            _viewState.update { it.copy(password = it.password.copy(error = "Required")) }
            return
        }

        _viewState.update { it.copy(loading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                signIn(email, password)

                _viewState.update { it.copy(complete = true) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                _viewState.update { it.copy(loading = false, errorMessage = exception.message) }
            }
        }
    }
}
