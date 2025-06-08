package nz.co.chrisdrake.receipts.domain.image

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toFile

class OpenImage(
    private val context: Context,
    private val getUriForFile: GetUriForFile,
) {

    operator fun invoke(uri: Uri) {
        val shareUri = if (uri.scheme == "file") {
            getUriForFile(uri.toFile())
        } else {
            uri
        }

        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(shareUri, "image/jpeg")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(viewIntent)
    }
}
