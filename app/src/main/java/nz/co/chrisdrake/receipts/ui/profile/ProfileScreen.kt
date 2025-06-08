package nz.co.chrisdrake.receipts.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import nz.co.chrisdrake.receipts.ui.theme.AppTheme

@Serializable
object ProfileRoute

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    navigateBack: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(viewState) {
        if (viewState.complete) {
            navigateBack()
        }
    }

    Content(
        viewState = viewState,
        navigateBack = navigateBack,
    )
}

@Composable
private fun Content(
    viewState: ProfileViewState,
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
        ) {
            Text(
                text = "Email",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = viewState.email,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewState.onClickSignOut,
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewState.loading,
            ) {
                if (viewState.loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign Out")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(visible = viewState.errorMessage != null) {
                viewState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
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
            Text(text = "Profile")
        },
        navigationIcon = {
            IconButton(onClick = dismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
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
            viewState = ProfileViewState(
                email = "user@example.com",
                onClickSignOut = {},
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
            viewState = ProfileViewState(
                email = "user@example.com",
                loading = true,
                onClickSignOut = {},
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
            viewState = ProfileViewState(
                email = "user@example.com",
                errorMessage = "Error",
                onClickSignOut = {},
            ),
            navigateBack = {},
        )
    }
}