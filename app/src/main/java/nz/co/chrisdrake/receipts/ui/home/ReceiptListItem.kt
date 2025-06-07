package nz.co.chrisdrake.receipts.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import nz.co.chrisdrake.receipts.domain.BackupStatus
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
    val backupStatus: BackupStatus,
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
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = receipt.merchant,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )

                BackupStatusIcon(status = receipt.backupStatus)

                Spacer(modifier = Modifier.width(8.dp))

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

@Composable
private fun BackupStatusIcon(status: BackupStatus) {
    Icon(
        imageVector = when (status) {
            BackupStatus.NotStarted -> Icons.Default.Download
            BackupStatus.InProgress -> Icons.Default.Downloading
            BackupStatus.Completed -> Icons.Default.CheckCircle
            BackupStatus.Failed -> Icons.Default.Error
        },
        contentDescription = when (status) {
            BackupStatus.NotStarted -> "Back up not started"
            BackupStatus.InProgress -> "Back up in progress"
            BackupStatus.Completed -> "Backed up"
            BackupStatus.Failed -> "Back up failed"
        },
    )
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
        backupStatus = BackupStatus.Completed,
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

@Preview
@Composable
private fun Preview_BackupStatusIcon() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BackupStatus.entries.forEach {
            BackupStatusIcon(status = it)
        }
    }
}