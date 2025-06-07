package nz.co.chrisdrake.receipts.domain

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CopyPictureToInternalStorage(
    private val context: Context,
    private val getPictureFile: GetPictureFile,
) {

    suspend operator fun invoke(uri: Uri, receiptId: String): Uri = withContext(Dispatchers.IO) {
        val target = getPictureFile(receiptId = receiptId)

        checkNotNull(context.contentResolver.openInputStream(uri)).use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        target.toUri()
    }
}