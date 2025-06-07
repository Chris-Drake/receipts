package nz.co.chrisdrake.receipts.data

import androidx.core.net.toUri
import nz.co.chrisdrake.receipts.domain.BackupStatus
import nz.co.chrisdrake.receipts.domain.Receipt
import nz.co.chrisdrake.receipts.domain.ReceiptItem
import java.time.LocalDate
import java.time.LocalTime

fun ReceiptEntity.toDomain(items: List<ReceiptItemEntity>): Receipt {
    return Receipt(
        id = id,
        imageUri = imageUri.toUri(),
        merchant = merchant,
        date = date,
        time = time,
        items = items.map(ReceiptItemEntity::toDomain),
        backUpStatus = backupStatus,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

private fun ReceiptItemEntity.toDomain(): ReceiptItem {
    return ReceiptItem(
        id = id,
        name = name,
        amount = amount
    )
}

fun ReceiptWithItemsEntity.toDomain(): Receipt {
    return receipt.toDomain(items)
}

fun Receipt.toEntity(): ReceiptWithItemsEntity {
    return ReceiptWithItemsEntity(
        receipt = ReceiptEntity(
            id = id,
            imageUri = imageUri.toString(),
            merchant = merchant,
            date = date,
            time = time,
            backupStatus = backUpStatus,
            createdAt = createdAt,
            updatedAt = updatedAt,
        ),
        items = items.map { it.toEntity(id) },
    )
}

private fun ReceiptItem.toEntity(receiptId: String): ReceiptItemEntity {
    return ReceiptItemEntity(
        id = id,
        receiptId = receiptId,
        name = name,
        amount = amount,
    )
}

fun Receipt.toRemoteEntity(imagePath: String): RemoteReceiptEntity {
    return RemoteReceiptEntity(
        id = id,
        imagePath = imagePath,
        merchant = merchant,
        date = date.toString(),
        time = time?.toString(),
        items = items.map {
            RemoteReceiptItemEntity(
                id = it.id,
                name = it.name,
                amount = it.amount.toString(),
            )
        },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun RemoteReceiptEntity.toDomain(): Receipt {
    return Receipt(
        id = id,
        imageUri = imagePath.toUri(),
        merchant = merchant,
        date = LocalDate.parse(date),
        time = time?.let { LocalTime.parse(it) },
        items = items.map {
            ReceiptItem(
                id = it.id,
                name = it.name,
                amount = it.amount.toBigDecimal(),
            )
        },
        createdAt = createdAt,
        updatedAt = updatedAt,
        backUpStatus = BackupStatus.Completed,
    )
}