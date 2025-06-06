package nz.co.chrisdrake.receipts.ui.receipt

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import nz.co.chrisdrake.receipts.ui.theme.AppTheme

@Serializable
object ReceiptRoute

@Composable
fun ReceiptScreen(
    dismiss: () -> Unit,
    viewModel: ReceiptViewModel = viewModel(),
) {
    val viewState = viewModel.viewState.collectAsState().value

    ReceiptContent(viewState = viewState, dismiss = dismiss)
}

@Composable
private fun ReceiptContent(
    viewState: ReceiptViewState,
    dismiss: () -> Unit,
) {
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = TakePicture(),
        onResult = viewState.onPictureResult,
    )

    LaunchedEffect(Unit) {
        takePictureLauncher.launch(viewState.uri)
    }

    LaunchedEffect(viewState) {
        if (viewState.dismissed) {
            dismiss()
        }
    }

    Scaffold(
        topBar = { TopBar(dismiss = dismiss) },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it)
                .padding(16.dp),
        ) {
            viewState.details?.let {
                ReceiptDetails(
                    viewState = viewState.details,
                    onClickImage = { takePictureLauncher.launch(viewState.uri) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(dismiss: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = "New receipt")
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
private fun Preview_ReceiptContent() {
    AppTheme {
        ReceiptContent(
            viewState = ReceiptViewState(
                uri = Uri.EMPTY,
                onPictureResult = {},
                details = null,
            ),
            dismiss = {},
        )
    }
}

