package nz.co.chrisdrake.receipts.ui.signup

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import nz.co.chrisdrake.receipts.ui.common.EmailInputField
import nz.co.chrisdrake.receipts.ui.common.InputFieldState
import nz.co.chrisdrake.receipts.ui.common.PasswordInputField
import nz.co.chrisdrake.receipts.ui.theme.AppTheme

@Serializable
object SignUpRoute

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = viewModel(),
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val autofillManager = LocalAutofillManager.current

    LaunchedEffect(viewState) {
        if (viewState.complete) {
            autofillManager?.commit()
            navigateToHome()
        }
    }

    Content(
        viewState = viewState,
        navigateBack = navigateBack,
    )
}

@Composable
private fun Content(
    viewState: SignUpViewState,
    navigateBack: () -> Unit,
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

            PasswordInputField(field = viewState.confirmPassword)

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
                onClick = viewState.onClickSignUp,
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewState.loading,
            ) {
                if (viewState.loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign Up")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(dismiss: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = "Sign Up")
        },
        navigationIcon = {
            IconButton(onClick = dismiss) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
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
            viewState = SignUpViewState(
                email = InputFieldState(label = "Email", onValueChanged = {}),
                password = InputFieldState(label = "Password", onValueChanged = {}),
                confirmPassword = InputFieldState(label = "Confirm Password", onValueChanged = {}),
                onClickSignUp = {},
            ),
            navigateBack = {},
        )
    }
}

@Preview
@Composable
private fun Preview_Content_Loading() {
    AppTheme {
        Content(
            viewState = SignUpViewState(
                loading = true,
                email = InputFieldState(label = "Email", onValueChanged = {}),
                password = InputFieldState(label = "Password", onValueChanged = {}),
                confirmPassword = InputFieldState(label = "Confirm Password", onValueChanged = {}),
                onClickSignUp = {},
            ),
            navigateBack = {},
        )
    }
}

@Preview
@Composable
private fun Preview_Content_Error() {
    AppTheme {
        Content(
            viewState = SignUpViewState(
                errorMessage = "Error",
                email = InputFieldState(label = "Email", onValueChanged = {}, error = "Required"),
                password = InputFieldState(label = "Password", onValueChanged = {}, error = "Required"),
                confirmPassword = InputFieldState(label = "Confirm Password", onValueChanged = {}, error = "Required"),
                onClickSignUp = {},
            ),
            navigateBack = {},
        )
    }
}