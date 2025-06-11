package nz.co.chrisdrake.receipts.domain.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nz.co.chrisdrake.receipts.domain.model.ReceiptImageFilePaths
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class CopyImagesToInternalStorage(
    private val context: Context,
    private val createImageFilePaths: CreateImageFilePaths,
) {

    suspend operator fun invoke(uri: Uri, receiptId: String): ReceiptImageFilePaths = withContext(Dispatchers.IO) {
        createImageFilePaths(receiptId = receiptId).also {
            copyPicture(uri = uri, target = File(checkNotNull(it.original)), maxHeight = 2000)
            copyPicture(uri = uri, target = File(it.thumbnail), maxHeight = 320)
        }
    }

    private fun copyPicture(uri: Uri, target: File, maxHeight: Int) {
        val inSampleSize = calculateInSampleSize(uri = uri, maxHeight = maxHeight)

        openInputStream(uri).use { input ->
            target.outputStream().use { output ->
                compressBitmap(input = input, output = output, inSampleSize = inSampleSize)
            }
        }
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

    private fun calculateInSampleSize(uri: Uri, maxHeight: Int): Int {
        openInputStream(uri).use { input ->
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            BitmapFactory.decodeStream(input, null, options)

            val originalHeight = options.outHeight

            return if (originalHeight > maxHeight) {
                originalHeight / maxHeight
            } else {
                1
            }
        }
    }

    private fun openInputStream(uri: Uri) =
        checkNotNull(context.contentResolver.openInputStream(uri))
}