package nz.co.chrisdrake.receipts.ui.signin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import nz.co.chrisdrake.receipts.R
import nz.co.chrisdrake.receipts.ui.common.EmailInputField
import nz.co.chrisdrake.receipts.ui.common.InputFieldState
import nz.co.chrisdrake.receipts.ui.common.PasswordInputField
import nz.co.chrisdrake.receipts.ui.theme.AppTheme

@Serializable
object SignInRoute

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = viewModel(),
    navigateBack: () -> Unit,
    navigateToSignUp: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val autofillManager = LocalAutofillManager.current

    LaunchedEffect(viewState) {
        if (viewState.complete) {
            autofillManager?.commit()
            navigateBack()
        }
    }

    Content(
        viewState = viewState,
        navigateBack = navigateBack,
        navigateToSignUp = navigateToSignUp,
    )
}

@Composable
private fun Content(
    viewState: SignInViewState,
    navigateBack: () -> Unit,
    navigateToSignUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(dismiss = navigateBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            EmailInputField(field = viewState.email)

            PasswordInputField(field = viewState.password)

            AnimatedVisibility(visible = viewState.errorMessage != null) {
                viewState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Button(
                onClick = viewState.onClickSignIn,
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewState.loading,
            ) {
                if (viewState.loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.sign_in_title))
                }
            }

            TextButton(
                onClick = navigateToSignUp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(stringResource(R.string.sign_in_sign_up_button))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(dismiss: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.sign_in_title))
        },
        navigationIcon = {
            IconButton(onClick = dismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.common_close),
                )
            }
        },
    )
}

@Preview
@Composable
private fun Preview_Content() {
    AppTheme {
        Content(
            viewState = SignInViewState(
                email = InputFieldState(label = stringResource(R.string.common_email_label), onValueChanged = {}),
                password = InputFieldState(label = stringResource(R.string.common_password_label), onValueChanged = {}),
                onClickSignIn = {},
            ),
            navigateBack = {},
            navigateToSignUp = {},
        )
    }
}

@Preview
@Composable
private fun Preview_Content_Loading() {
    AppTheme {
        Content(
            viewState = SignInViewState(
                loading = true,
                email = InputFieldState(label = stringResource(R.string.common_email_label), onValueChanged = {}),
                password = InputFieldState(label = stringResource(R.string.common_password_label), onValueChanged = {}),
                onClickSignIn = {},
            ),
            navigateBack = {},
            navigateToSignUp = {},
        )
    }
}

@Preview
@Composable
private fun Preview_Content_Error() {
    AppTheme {
        Content(
            viewState = SignInViewState(
                errorMessage = "Error",
                email = InputFieldState(
                    label = stringResource(R.string.common_email_label),
                    onValueChanged = {},
                    error = stringResource(R.string.common_input_field_required),
                ),
                password = InputFieldState(
                    label = stringResource(R.string.common_password_label),
                    onValueChanged = {},
                    error = stringResource(R.string.common_input_field_required),
                ),
                onClickSignIn = {},
            ),
            navigateBack = {},
            navigateToSignUp = {},
        )
    }
}
