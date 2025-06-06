package nz.co.chrisdrake.receipts.domain

import nz.co.chrisdrake.receipts.data.ReceiptRepository

class SaveReceipt(private val repository: ReceiptRepository) {

    suspend operator fun invoke(receipt: Receipt) = repository.saveReceipt(receipt)
}