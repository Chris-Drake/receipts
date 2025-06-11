package nz.co.chrisdrake.receipts.domain

import android.net.Uri
import androidx.core.net.toUri
import nz.co.chrisdrake.receipts.data.RemoteDataSource
import nz.co.chrisdrake.receipts.domain.model.Receipt
import java.io.File

class GetOriginalImageUri(private val remoteDataSource: RemoteDataSource) {

    suspend operator fun invoke(receipt: Receipt): Uri {
        val fileUri = receipt.imageFilePaths
            ?.original
            ?.let(::File)
            ?.toUri()

        return fileUri
            ?: remoteDataSource.getImageDownloadUrl(receipt.imageDownloadPaths!!.original)
    }
}