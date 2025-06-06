package nz.co.chrisdrake.receipts.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import nz.co.chrisdrake.receipts.ui.theme.AppTheme

@Serializable
object HomeRoute

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navigateToReceipt: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    Content(
        viewState = viewState,
        navigateToReceipt = navigateToReceipt,
    )
}

@Composable
private fun Content(
    viewState: HomeViewState,
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
        when {
            viewState.receipts.isEmpty() -> {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                    }

                    items(viewState.receipts, key = ReceiptListItem::id) {
                        ReceiptListItem(
                            receipt = it,
                            modifier = Modifier.fillMaxWidth().animateItem(),
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No receipts yet",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview
@Composable
private fun Content_WithReceipts() {
    AppTheme {
        Content(
            viewState = HomeViewState(receipts = listOf(preview_ReceiptListItem())),
            navigateToReceipt = {},
        )
    }
}

@Preview
@Composable
private fun Content_Empty() {
    AppTheme {
        Content(
            viewState = HomeViewState(receipts = emptyList()),
            navigateToReceipt = {},
        )
    }
}
