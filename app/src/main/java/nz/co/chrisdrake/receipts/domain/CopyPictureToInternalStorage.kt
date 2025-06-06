package nz.co.chrisdrake.receipts.domain

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class CopyPictureToInternalStorage(private val context: Context) {

    suspend operator fun invoke(uri: Uri, receiptId: String): Uri = withContext(Dispatchers.IO) {
        val storageDirectory = File(context.filesDir, "receipts")

        check(storageDirectory.exists() || storageDirectory.mkdirs())

        val target = File(storageDirectory, "receipt_${receiptId}_${UUID.randomUUID()}.jpg")

        checkNotNull(context.contentResolver.openInputStream(uri)).use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        target.toUri()
    }
}