package nz.co.chrisdrake.receipts.domain

import nz.co.chrisdrake.receipts.data.ReceiptRepository

class GetReceipt(private val repository: ReceiptRepository) {

    suspend operator fun invoke(id: ReceiptId): Receipt = repository.getReceipt(id = id)
}
