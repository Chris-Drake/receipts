package nz.co.chrisdrake.receipts.domain

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED
import nz.co.chrisdrake.receipts.domain.model.BackupStatus
import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.domain.model.ReceiptImageDownloadPaths
import nz.co.chrisdrake.receipts.domain.model.ReceiptImageFilePaths
import nz.co.chrisdrake.receipts.domain.model.ReceiptItem
import nz.co.chrisdrake.receipts.domain.model.User
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

fun createItem(
    id: String = "123",
    name: String = "item",
    amount: BigDecimal = BigDecimal.ONE,
) = ReceiptItem(
    id = id,
    name = name,
    amount = amount,
)

fun createReceipt(
    id: String = "id",
    merchant: String = "merchant",
    date: LocalDate = LocalDate.of(2025, Month.JANUARY, 1),
    backupStatus: BackupStatus = mock(),
    imageFilePaths: ReceiptImageFilePaths? = createReceiptImageFilePaths(),
    imageDownloadPaths: ReceiptImageDownloadPaths = mock(),
    updatedAt: Long = 0L,
    items: List<ReceiptItem> = listOf(createItem()),
) = Receipt(
    id = id,
    merchant = merchant,
    date = date,
    time = null,
    items = items,
    imageFilePaths = imageFilePaths,
    imageDownloadPaths = imageDownloadPaths,
    backUpStatus = backupStatus,
    createdAt = 0L,
    updatedAt = updatedAt,
    accessedAt = null,
)

private fun createReceiptImageFilePaths() = ReceiptImageFilePaths(
    original = "local/original.jpg",
    thumbnail = "local/thumb.jpg",
)

fun createUser() = User(
    id = "123",
    email = "example@example.com",
)

fun connectivityManager(network: Network = mock(), unmetered: Boolean): ConnectivityManager {
    val networkCapabilities = mock<NetworkCapabilities> {
        on { hasCapability(NET_CAPABILITY_NOT_METERED) } doReturn unmetered
    }

    return mock<ConnectivityManager> {
        on { activeNetwork } doReturn network
        on { getNetworkCapabilities(network) } doReturn networkCapabilities
    }
}