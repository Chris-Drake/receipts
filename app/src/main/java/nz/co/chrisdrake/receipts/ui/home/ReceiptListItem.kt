package nz.co.chrisdrake.receipts.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
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
    val dateTime: String,
    val itemCount: String,
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
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = receipt.merchant,
                    style = typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = Ellipsis,
                )

                Text(
                    text = receipt.dateTime,
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = Ellipsis,
                )

                Text(
                    text = receipt.itemCount,
                    style = typography.bodyMedium,
                    maxLines = 1,
                    overflow = Ellipsis,
                )

                Text(
                    text = receipt.totalAmount,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    maxLines = 1,
                    overflow = Ellipsis,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Image(uri = receipt.imageUri, backupStatus = receipt.backupStatus)
        }
    }
}

@Composable
private fun Image(uri: String, backupStatus: BackupStatus) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(0.75f),
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Receipt image",
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )

        BackupStatusIcon(
            status = backupStatus,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        )
    }
}

@Composable
private fun BackupStatusIcon(status: BackupStatus, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp)
            .background(color = colorScheme.surface, shape = CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = when (status) {
                BackupStatus.NotStarted -> Icons.Default.Download
                BackupStatus.InProgress -> Icons.Default.Downloading
                BackupStatus.Completed -> Icons.Default.Check
                BackupStatus.Failed -> Icons.Default.ErrorOutline
            },
            contentDescription = when (status) {
                BackupStatus.NotStarted -> "Back up not started"
                BackupStatus.InProgress -> "Back up in progress"
                BackupStatus.Completed -> "Backed up"
                BackupStatus.Failed -> "Back up failed"
            },
            modifier = Modifier.fillMaxSize().padding(4.dp),
            tint = colorScheme.onSurface,
        )
    }
}

@Suppress("FunctionName")
fun preview_ReceiptListItem(): ReceiptListItem {
    return ReceiptListItem(
        id = "123",
        merchant = "Grocery Store",
        dateTime = "6 Jun 2025 at 2:30 PM",
        itemCount = "2 items",
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
