package nz.co.chrisdrake.receipts.data

import nz.co.chrisdrake.receipts.domain.model.BackupStatus
import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.domain.model.ReceiptImageDownloadPaths
import nz.co.chrisdrake.receipts.domain.model.ReceiptImageFilePaths
import nz.co.chrisdrake.receipts.domain.model.ReceiptItem
import java.time.LocalDate
import java.time.LocalTime

fun ReceiptEntity.toDomain(items: List<ReceiptItemEntity>): Receipt {
    return Receipt(
        id = id,
        imageFilePaths = ReceiptImageFilePaths(
            original = imagePath,
            thumbnail = thumbnailPath,
        ),
        imageDownloadPaths = imageDownloadPath?.let {
            thumbnailDownloadPath?.let {
                ReceiptImageDownloadPaths(
                    original = imageDownloadPath,
                    thumbnail = thumbnailDownloadPath,
                )
            }
        },
        merchant = merchant,
        date = date,
        time = time,
        items = items.map(ReceiptItemEntity::toDomain),
        backUpStatus = backupStatus,
        createdAt = createdAt,
        updatedAt = updatedAt,
        accessedAt = accessedAt,
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
            thumbnailPath = checkNotNull(imageFilePaths).thumbnail,
            thumbnailDownloadPath = imageDownloadPaths?.thumbnail,
            imagePath = imageFilePaths.original,
            imageDownloadPath = imageDownloadPaths?.original,
            merchant = merchant,
            date = date,
            time = time,
            backupStatus = backUpStatus,
            createdAt = createdAt,
            updatedAt = updatedAt,
            accessedAt = accessedAt,
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

fun Receipt.toRemoteEntity(downloadPaths: ReceiptImageDownloadPaths): RemoteReceiptEntity {
    return RemoteReceiptEntity(
        id = id,
        thumbnailDownloadPath = downloadPaths.thumbnail,
        imageDownloadPath = downloadPaths.original,
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
        imageFilePaths = null,
        imageDownloadPaths = ReceiptImageDownloadPaths(
            original = imageDownloadPath,
            thumbnail = thumbnailDownloadPath,
        ),
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
        accessedAt = null,
    )
}