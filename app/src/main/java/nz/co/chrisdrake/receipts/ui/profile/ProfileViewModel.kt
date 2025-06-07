package nz.co.chrisdrake.receipts.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import nz.co.chrisdrake.receipts.DependencyRegistry.get
import nz.co.chrisdrake.receipts.domain.GetCurrentUser
import nz.co.chrisdrake.receipts.domain.SignOut
import kotlin.coroutines.cancellation.CancellationException

class ProfileViewModel(
    getCurrentUser: GetCurrentUser = get(),
    private val signOut: SignOut = get(),
) : ViewModel() {

    private val currentUser = checkNotNull(getCurrentUser())

    private val _viewState = MutableStateFlow(
        ProfileViewState(
            email = currentUser.email,
            onClickSignOut = ::attemptSignOut,
        )
    )

    val viewState: StateFlow<ProfileViewState> = _viewState

    private fun attemptSignOut() {
        _viewState.update { it.copy(loading = true) }

        try {
            signOut()

            _viewState.update { it.copy(complete = true) }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            _viewState.update { it.copy(loading = false, errorMessage = exception.message) }
        }
    }
}