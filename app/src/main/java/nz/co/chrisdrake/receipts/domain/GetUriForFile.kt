package nz.co.chrisdrake.receipts.domain

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class GetUriForFile(private val context: Context) {

    operator fun invoke(file: File): Uri {
        return FileProvider.getUriForFile(context, context.packageName + ".provider", file)
    }
}