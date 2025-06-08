package nz.co.chrisdrake.receipts.domain.image

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

class GetTempImageUri(
    private val context: Context,
    private val getUriForFile: GetUriForFile,
) {

    operator fun invoke(): Uri {
        val storageDirectory = File(context.filesDir, "tmp")

        check(storageDirectory.exists() || storageDirectory.mkdirs())

        val imageFile = File.createTempFile(
            "receipt_${UUID.randomUUID()}",
            ".jpg",
            storageDirectory,
        )

        imageFile.deleteOnExit()

        return getUriForFile(imageFile)
    }
}