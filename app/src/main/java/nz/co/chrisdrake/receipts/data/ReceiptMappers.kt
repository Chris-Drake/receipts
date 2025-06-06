package nz.co.chrisdrake.receipts.data

import androidx.core.net.toUri
import nz.co.chrisdrake.receipts.domain.Receipt
import nz.co.chrisdrake.receipts.domain.ReceiptItem

fun ReceiptEntity.toDomain(items: List<ReceiptItemEntity>): Receipt {
    return Receipt(
        id = id,
        imageUri = imageUri.toUri(),
        merchant = merchant,
        date = date,
        time = time,
        items = items.map(ReceiptItemEntity::toDomain),
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