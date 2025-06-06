package nz.co.chrisdrake.receipts.ui.receipt

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import nz.co.chrisdrake.receipts.ui.common.DateField
import nz.co.chrisdrake.receipts.ui.common.DateFieldState
import nz.co.chrisdrake.receipts.ui.common.InputField
import nz.co.chrisdrake.receipts.ui.common.TimeField
import nz.co.chrisdrake.receipts.ui.common.TimeFieldState
import nz.co.chrisdrake.receipts.ui.common.preview_InputFieldState
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptViewState.Details
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptViewState.Item
import nz.co.chrisdrake.receipts.ui.theme.AppTheme

@Composable
fun ReceiptDetails(
    viewState: Details,
    onClickImage: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ReceiptImage(uri = viewState.imageUri, onClick = onClickImage)

        DetailsSection(viewState = viewState)

        ItemsSection(viewState = viewState)

        Spacer(modifier = Modifier.weight(1f))

        SaveButton(onClick = viewState.onClickSave)
    }
}

@Composable
private fun ReceiptImage(uri: Uri, onClick: () -> Unit) {
    AsyncImage(
        model = uri,
        contentDescription = "Receipt image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun DetailsSection(viewState: Details) {
    Text(
        text = "Details",
        style = typography.titleLarge,
    )

    InputField(field = viewState.merchant)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        DateField(
            field = viewState.date,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )

        TimeField(
            field = viewState.time,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
    }
}

@Composable
private fun ColumnScope.ItemsSection(viewState: Details) {
    Text(
        text = "Items",
        style = typography.titleMedium,
    )

    viewState.items.forEach {
        Item(item = it)
    }

    viewState.itemsError?.let { error ->
        Text(
            text = error,
            color = colorScheme.error,
            style = typography.bodyMedium,
        )
    }

    AddItemButton(
        onClick = viewState.onClickAddItem,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
}

@Composable
private fun Item(item: Item) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        InputField(field = item.name, modifier = Modifier.weight(1.5f))

        InputField(
            field = item.amount,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        IconButton(onClick = item.onClickDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete item",
            )
        }
    }
}

@Composable
private fun AddItemButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
        )
        Text(text = "Add Item")
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Save")
    }
}

@Preview
@Composable
private fun Preview_ReceiptDetails() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ReceiptDetails(
                viewState = Details(
                    imageUri = Uri.EMPTY,
                    merchant = preview_InputFieldState(label = "Merchant"),
                    date = DateFieldState(onDateSelected = {}),
                    time = TimeFieldState(onTimeSelected = {}),
                    items = List(2) { index ->
                        Item(
                            id = index.toString(),
                            name = preview_InputFieldState(label = "Item"),
                            amount = preview_InputFieldState(label = "Amount"),
                            onClickDelete = {},
                        )
                    },
                    onClickAddItem = {},
                    onClickSave = {},
                ),
                onClickImage = {},
            )
        }
    }
}