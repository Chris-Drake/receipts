package nz.co.chrisdrake.receipts.domain.image

import android.content.Context
import java.io.File
import java.util.UUID

class GetPictureFile(private val context: Context) {

    operator fun invoke(receiptId: String): File {
        val storageDirectory = File(context.filesDir, "receipts")

        check(storageDirectory.exists() || storageDirectory.mkdirs())

        return File(storageDirectory, "receipt_${receiptId}_${UUID.randomUUID()}.jpg")
    }
}