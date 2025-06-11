package nz.co.chrisdrake.receipts.domain

import nz.co.chrisdrake.receipts.data.ReceiptRepository
import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.domain.model.ReceiptId

class GetReceipt(private val repository: ReceiptRepository) {

    suspend operator fun invoke(id: ReceiptId): Receipt {
        val receipt = checkNotNull(repository.getReceipt(id = id))

        repository.updateReceipt(receipt.copy(accessedAt = System.currentTimeMillis()))

        return receipt
    }
}