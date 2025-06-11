package nz.co.chrisdrake.receipts.domain.image

import android.content.Context
import nz.co.chrisdrake.receipts.domain.model.ReceiptImageFilePaths
import java.io.File
import java.util.UUID

class CreateImageFilePaths(private val context: Context) {

    operator fun invoke(receiptId: String): ReceiptImageFilePaths {
        val imageId = UUID.randomUUID().toString()
        val storageDirectory = File(context.filesDir, "receipts/$receiptId")

        check(storageDirectory.exists() || storageDirectory.mkdirs())

        return ReceiptImageFilePaths(
            original = File(storageDirectory, "$imageId-original.jpg").path,
            thumbnail = File(storageDirectory, "$imageId-thumb.jpg").path,
        )
    }
}