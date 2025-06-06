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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import nz.co.chrisdrake.receipts.domain.ReceiptId
import nz.co.chrisdrake.receipts.ui.theme.AppTheme

@Serializable
data class ReceiptRoute(val id: ReceiptId?)

@Composable
fun ReceiptScreen(
    id: ReceiptId?,
    dismiss: () -> Unit,
    viewModel: ReceiptViewModel = viewModel { ReceiptViewModel(existingId = id) },
) {
    val viewState = viewModel.viewState.collectAsState().value

    ReceiptContent(id = id, viewState = viewState, dismiss = dismiss)
}

@Composable
private fun ReceiptContent(
    id: ReceiptId?,
    viewState: ReceiptViewState,
    dismiss: () -> Unit,
) {
    var tempImageUri by remember {
        mutableStateOf(viewState.createTempImageUri())
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = TakePicture(),
        onResult = { viewState.onPictureResult(tempImageUri, it) },
    )

    LaunchedEffect(Unit) {
        if (id == null) {
            takePictureLauncher.launch(tempImageUri)
        }
    }

    LaunchedEffect(viewState) {
        if (viewState.dismissed) {
            dismiss()
        }
    }

    Scaffold(
        topBar = { TopBar(title = viewState.title, dismiss = dismiss) },
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
                    onClickImage = {
                        tempImageUri = viewState.createTempImageUri()
                        takePictureLauncher.launch(tempImageUri)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(title: String, dismiss: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = title)
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
            id = null,
            viewState = ReceiptViewState(
                title = "New receipt",
                createTempImageUri = { Uri.EMPTY },
                onPictureResult = { _, _ -> },
                details = null,
            ),
            dismiss = {},
        )
    }
}

