package nz.co.chrisdrake.receipts.ui.signin

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import nz.co.chrisdrake.receipts.R
import nz.co.chrisdrake.receipts.domain.auth.SignIn
import nz.co.chrisdrake.receipts.util.ResourceProvider
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SignInViewModelTest {

    private val signIn = mock<SignIn>()
    private val resourceProvider = mock<ResourceProvider> {
        on { getString(R.string.common_email_label) } doReturn "Email"
        on { getString(R.string.common_password_label) } doReturn "Password"
        on { getString(R.string.common_input_field_required) } doReturn "Required"
    }

    private val viewModel = SignInViewModel(
        signIn = signIn,
        resourceProvider = resourceProvider,
    )

    @Test
    fun `initial state is correct`() {
        with(viewModel.viewState.value) {
            assertThat(email.label).isEqualTo("Email")
            assertThat(password.label).isEqualTo("Password")
            assertThat(loading).isFalse()
            assertThat(complete).isFalse()
            assertThat(errorMessage).isNull()
        }
    }

    @Test
    fun `shows error when email is empty`() = runTest {
        viewModel.viewState.test {
            awaitItem().onClickSignIn()
            assertThat(awaitItem().email.error).isEqualTo("Required")
        }
    }

    @Test
    fun `shows error when password is empty`() = runTest {
        viewModel.viewState.test {
            awaitItem().email.onValueChanged("test@example.com")
            awaitItem().onClickSignIn()
            assertThat(awaitItem().password.error).isEqualTo("Required")
        }
    }

    @Test
    fun `sign in success updates state to complete`() = runTest {
        viewModel.viewState.test {
            awaitItem().email.onValueChanged("test@example.com")
            awaitItem().password.onValueChanged("password123")
            awaitItem().onClickSignIn()
            assertThat(awaitItem().loading).isTrue()
            assertThat(awaitItem().complete).isTrue()
            verify(signIn).invoke("test@example.com", "password123")
        }
    }

    @Test
    fun `sign in failure shows error message`() = runTest {
        whenever(signIn("test@example.com", "password123"))
            .thenThrow(RuntimeException("Sign in failed"))

        viewModel.viewState.test {
            awaitItem().email.onValueChanged("test@example.com")
            awaitItem().password.onValueChanged("password123")
            awaitItem().onClickSignIn()
            assertThat(awaitItem().loading).isTrue()
            with(awaitItem()) {
                assertThat(errorMessage).isEqualTo("Sign in failed")
                assertThat(loading).isFalse()
                assertThat(complete).isFalse()
            }
        }
    }
}