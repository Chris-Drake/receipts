package nz.co.chrisdrake.receipts.domain

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

class GetTempImageUri(private val context: Context) {

    operator fun invoke(): Uri {
        val storageDirectory = File(context.filesDir, "tmp")

        check(storageDirectory.exists() || storageDirectory.mkdirs())

        val imageFile = File.createTempFile(
            "receipt_${UUID.randomUUID()}",
            ".jpg",
            storageDirectory,
        )

        imageFile.deleteOnExit()

        return FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
    }
}