package nz.co.chrisdrake.receipts.domain

import nz.co.chrisdrake.receipts.data.ReceiptRepository

class DeleteReceipt(
    private val getCurrentUser: GetCurrentUser,
    private val repository: ReceiptRepository,
) {

    suspend operator fun invoke(id: ReceiptId) {
        repository.deleteReceipt(
            userId = getCurrentUser()?.id,
            id = id,
        )
    }
}