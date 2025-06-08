package nz.co.chrisdrake.receipts.ui.receipt

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInFull
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
import androidx.compose.ui.draw.clip
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month

@Composable
fun ReceiptDetails(
    viewState: Details,
    onClickImage: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ReceiptImage(
            uri = viewState.imageUri,
            onClick = onClickImage,
            onClickOpen = viewState.onClickOpenImage,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        DetailsSection(viewState = viewState)

        ItemsSection(viewState = viewState)

        Spacer(modifier = Modifier.weight(1f))

        if (viewState.editing) {
            SaveButton(onClick = viewState.onClickSave)
        } else {
            EditButton(onClick = viewState.onClickEdit)
        }
    }
}

@Composable
private fun ReceiptImage(
    uri: Uri,
    onClick: () -> Unit,
    onClickOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = uri,
            contentDescription = "Receipt image",
            modifier = Modifier
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
            contentScale = ContentScale.FillHeight,
        )

        IconButton(
            onClick = onClickOpen,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(color = colorScheme.surface, shape = CircleShape),
        ) {
            Icon(
                imageVector = Icons.Default.OpenInFull,
                contentDescription = "Open receipt image",
            )
        }
    }
}

@Composable
private fun DetailsSection(viewState: Details) {
    Spacer(modifier = Modifier.height(8.dp))

    AnimatedContent(viewState.editing) {
        if (it) {
            EditableDetailsSection(viewState = viewState)
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = viewState.merchant.value,
                    style = typography.displaySmall,
                )

                Text(
                    text = viewState.formattedDateTime,
                    style = typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun EditableDetailsSection(viewState: Details) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
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
}

@Composable
private fun ColumnScope.ItemsSection(viewState: Details) {
    Text(
        text = "Items",
        style = typography.titleMedium,
    )

    viewState.items.forEach {
        Item(item = it, editing = viewState.editing)
    }

    AnimatedVisibility(viewState.formattedTotal != null) {
        viewState.formattedTotal?.let {
            LabelValue(
                label = "Total",
                value = it,
                modifier = Modifier.padding(vertical = if (viewState.editing) 8.dp else 0.dp),
            )
        }
    }

    viewState.itemsError?.let { error ->
        Text(
            text = error,
            color = colorScheme.error,
            style = typography.bodyMedium,
        )
    }

    if (viewState.editing) {
        AddItemButton(
            onClick = viewState.onClickAddItem,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun Item(item: Item, editing: Boolean) = with(item) {
    AnimatedContent(editing) {
        if (it) {
            EditableItem(item = item)
        } else {
            LabelValue(label = name.value, value = "$" + amount.value)
        }
    }
}

@Composable
private fun EditableItem(item: Item) {
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
private fun LabelValue(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = typography.labelMedium,
        )

        Text(
            text = value,
            style = typography.bodyMedium,
        )
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

@Composable
private fun EditButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Edit")
    }
}

@Suppress("FunctionName")
private fun preview_ReceiptDetails() = Details(
    imageUri = Uri.EMPTY,
    merchant = preview_InputFieldState(label = "Merchant", value = "Starbucks"),
    date = DateFieldState(selection = LocalDate.of(1970, Month.JANUARY, 1), onDateSelected = {}),
    time = TimeFieldState(selection = LocalTime.of(12, 0), onTimeSelected = {}),
    items = List(2) { index ->
        Item(
            id = index.toString(),
            name = preview_InputFieldState(label = "Item", value = "Latte"),
            amount = preview_InputFieldState(label = "Amount", value = "4.50"),
            onClickDelete = {},
        )
    },
    editing = false,
    onClickAddItem = {},
    onClickSave = {},
    onClickEdit = {},
    onClickOpenImage = {},
)

@Preview
@Composable
private fun Preview_ReceiptDetails() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ReceiptDetails(
                viewState = preview_ReceiptDetails(),
                onClickImage = {},
            )
        }
    }
}

@Preview
@Composable
private fun Preview_ReceiptDetails_Editing() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ReceiptDetails(
                viewState = preview_ReceiptDetails().copy(editing = true),
                onClickImage = {},
            )
        }
    }
}