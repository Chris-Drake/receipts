package nz.co.chrisdrake.receipts.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import nz.co.chrisdrake.receipts.domain.ReceiptId
import nz.co.chrisdrake.receipts.ui.theme.AppTheme

data class ReceiptListItem(
    val id: ReceiptId,
    val imageUri: String,
    val merchant: String,
    val date: String,
    val time: String?,
    val itemCount: Int,
    val totalAmount: String,
)

@Composable
fun ReceiptListItem(
    receipt: ReceiptListItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = onClick,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = receipt.merchant,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = receipt.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (receipt.time != null) {
                Text(
                    text = receipt.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${receipt.itemCount} item${if (receipt.itemCount == 1) "" else "s"}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = receipt.totalAmount,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = receipt.imageUri,
                contentDescription = "Receipt image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Suppress("FunctionName")
fun preview_ReceiptListItem(): ReceiptListItem {
    return ReceiptListItem(
        id = "123",
        merchant = "Grocery Store",
        date = "6 Jun 2025",
        time = "2:30 PM",
        itemCount = 2,
        totalAmount = "$6.49",
        imageUri = "",
    )
}

@Preview
@Composable
fun Preview_ReceiptListItem() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ReceiptListItem(receipt = preview_ReceiptListItem())
        }
    }
}
