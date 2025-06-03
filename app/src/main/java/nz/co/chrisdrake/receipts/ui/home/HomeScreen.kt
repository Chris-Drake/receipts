package nz.co.chrisdrake.receipts.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navigateToReceipt: () -> Unit,
) {
    Content(
        navigateToReceipt = navigateToReceipt,
    )
}

@Composable
private fun Content(
    navigateToReceipt: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToReceipt) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
    }
}

@Preview
@Composable
private fun Preview_HomeScreenContent() {
    Content(
        navigateToReceipt = {},
    )
}