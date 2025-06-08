package nz.co.chrisdrake.receipts.domain.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.InputStream

class CopyPictureToInternalStorage(
    private val context: Context,
    private val getPictureFile: GetPictureFile,
) {

    suspend operator fun invoke(uri: Uri, receiptId: String): Uri = withContext(Dispatchers.IO) {
        val target = getPictureFile(receiptId = receiptId)
        val inSampleSize = calculateInSampleSize(uri)

        openInputStream(uri).use { input ->
            target.outputStream().use { output ->
                compressBitmap(input = input, output = output, inSampleSize = inSampleSize)
            }
        }

        target.toUri()
    }

    private fun compressBitmap(input: InputStream, output: FileOutputStream, inSampleSize: Int) {
        val decodeOptions = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
        }

        with(checkNotNull(BitmapFactory.decodeStream(input, null, decodeOptions))) {
            compress(Bitmap.CompressFormat.JPEG, 80, output)
            recycle()
        }
    }

    private fun calculateInSampleSize(uri: Uri, maxWidth: Int = 1500): Int {
        openInputStream(uri).use { input ->
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            BitmapFactory.decodeStream(input, null, options)

            val originalWidth = options.outWidth

            return if (originalWidth > maxWidth) {
                originalWidth / maxWidth
            } else {
                1
            }
        }
    }

    private fun openInputStream(uri: Uri) =
        checkNotNull(context.contentResolver.openInputStream(uri))
}