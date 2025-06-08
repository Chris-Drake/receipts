package nz.co.chrisdrake.receipts.domain

import nz.co.chrisdrake.receipts.data.ReceiptRepository
import nz.co.chrisdrake.receipts.domain.model.Receipt

class UpdateReceipt(private val repository: ReceiptRepository) {

    suspend operator fun invoke(receipt: Receipt) = repository.updateReceipt(receipt = receipt)
}