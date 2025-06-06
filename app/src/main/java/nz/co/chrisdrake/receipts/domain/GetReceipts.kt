package nz.co.chrisdrake.receipts.domain

import kotlinx.coroutines.flow.Flow
import nz.co.chrisdrake.receipts.data.ReceiptRepository

class GetReceipts(private val repository: ReceiptRepository) {

    operator fun invoke(): Flow<List<Receipt>> = repository.getReceipts()
}
