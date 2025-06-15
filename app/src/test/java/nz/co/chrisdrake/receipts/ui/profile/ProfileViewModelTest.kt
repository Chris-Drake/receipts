package nz.co.chrisdrake.receipts.ui.profile

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import nz.co.chrisdrake.receipts.domain.auth.GetCurrentUser
import nz.co.chrisdrake.receipts.domain.auth.SignOut
import nz.co.chrisdrake.receipts.domain.createUser
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileViewModelTest {

    private val user = createUser()
    private val signOut = mock<SignOut>()
    private val getCurrentUser = mock<GetCurrentUser> {
        on { invoke() } doReturn user
    }

    private val viewModel = ProfileViewModel(
        getCurrentUser = getCurrentUser,
        signOut = signOut,
    )

    @Test
    fun `initial state contains user email`() {
        with(viewModel.viewState.value) {
            assertThat(email).isEqualTo(user.email)
            assertThat(errorMessage).isNull()
            assertThat(loading).isFalse()
            assertThat(complete).isFalse()
        }
    }

    @Test
    fun `user can sign out successfully`() = runTest {
        viewModel.viewState.test {
            awaitItem().onClickSignOut()
            assertThat(awaitItem().loading).isTrue()
            assertThat(awaitItem().complete).isTrue()
            verify(signOut).invoke()
        }
    }

    @Test
    fun `error shown when sign out is unsuccessful`() = runTest {
        whenever(signOut()).thenThrow(RuntimeException("Sign out failed"))

        viewModel.viewState.test {
            awaitItem().onClickSignOut()
            assertThat(awaitItem().loading).isTrue()

            with(awaitItem()) {
                assertThat(errorMessage).isEqualTo("Sign out failed")
                assertThat(loading).isFalse()
                assertThat(complete).isFalse()
            }
        }
    }
}