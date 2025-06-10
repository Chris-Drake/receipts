package nz.co.chrisdrake.receipts.ui.home.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nz.co.chrisdrake.receipts.domain.model.ReceiptId
import nz.co.chrisdrake.receipts.ui.home.ReceiptListItem
import nz.co.chrisdrake.receipts.ui.home.preview_ReceiptListItem
import nz.co.chrisdrake.receipts.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    state: SearchBarState,
    navigateToReceipt: (id: ReceiptId) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.expanded) {
        if (state.expanded) {
            focusRequester.requestFocus()
        }
    }

    androidx.compose.material3.SearchBar(
        inputField = { SearchBarInputField(state = state, focusRequester = focusRequester) },
        expanded = state.expanded,
        onExpandedChange = state.onExpandedChange,
        content = {
            SearchResults(results = state.results, onClick = navigateToReceipt)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarInputField(
    state: SearchBarState,
    focusRequester: FocusRequester,
) = with(state) {
    SearchBarDefaults.InputField(
        modifier = Modifier.focusRequester(focusRequester),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = {},
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        placeholder = { Text("Search") },
        leadingIcon = {
            IconButton(onClick = { onExpandedChange(false) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
    )
}

@Composable
private fun SearchResults(
    results: List<ReceiptListItem>,
    onClick: (id: ReceiptId) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(results, key = ReceiptListItem::id) {
            ReceiptListItem(
                receipt = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                onClick = { onClick(it.id) },
            )
        }
    }
}

@Preview
@Composable
private fun Preview_SearchBar() {
    AppTheme {
        SearchBar(
            state = SearchBarState(
                query = "store",
                onQueryChange = {},
                expanded = true,
                onExpandedChange = {},
                results = listOf(preview_ReceiptListItem()),
            ),
            navigateToReceipt = {},
        )
    }
}