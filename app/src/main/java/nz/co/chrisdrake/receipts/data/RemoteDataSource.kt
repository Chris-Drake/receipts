package nz.co.chrisdrake.receipts.data

import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import nz.co.chrisdrake.receipts.data.RemoteReceiptEntity.Companion.UPDATED_AT_PROPERTY_NAME
import nz.co.chrisdrake.receipts.domain.model.Receipt
import nz.co.chrisdrake.receipts.domain.model.ReceiptId
import nz.co.chrisdrake.receipts.domain.model.ReceiptImageDownloadPaths
import java.io.File

class RemoteDataSource {

    suspend fun getReceipts(userId: String, updatedAfter: Long): List<Receipt> {
        return getReceiptsCollectionRef(userId)
            .whereGreaterThan(UPDATED_AT_PROPERTY_NAME, updatedAfter).get()
            .await()
            .map { document -> document.toObject(RemoteReceiptEntity::class.java).toDomain() }
    }

    suspend fun uploadImages(userId: String, receipt: Receipt): ReceiptImageDownloadPaths {
        val imageFilePaths = checkNotNull(receipt.imageFilePaths)

        val imageDownloadPath = storeImage(
            userId = userId,
            receiptId = receipt.id,
            fileUri = File(checkNotNull(imageFilePaths.original)).toUri(),
        )

        val thumbnailDownloadPath = storeImage(
            userId = userId,
            receiptId = receipt.id,
            fileUri = File(imageFilePaths.thumbnail).toUri(),
        )

        return ReceiptImageDownloadPaths(
            original = imageDownloadPath,
            thumbnail = thumbnailDownloadPath,
        )
    }

    suspend fun saveReceipt(
        userId: String,
        receipt: Receipt,
        downloadPaths: ReceiptImageDownloadPaths,
    ) {
        val remoteEntity = receipt.toRemoteEntity(downloadPaths = downloadPaths)

        getReceiptDocumentRef(userId = userId, receiptId = receipt.id)
            .set(remoteEntity)
            .await()
    }

    private suspend fun storeImage(userId: String, receiptId: ReceiptId, fileUri: Uri): String {
        val imageFile = fileUri.toFile()
        val imageRef = Firebase.storage.reference
            .child("/users/${userId}/receipts/${receiptId}/${imageFile.name}")

        imageRef.putFile(fileUri).await()

        return imageRef.path
    }

    suspend fun getImageDownloadUrl(path: String): Uri {
        return Firebase.storage.reference.child(path).downloadUrl.await()
    }

    suspend fun getImage(path: String, destinationFile: File) {
        Firebase.storage.reference.child(path).getFile(destinationFile).await()
    }

    suspend fun deleteReceipt(userId: String, id: ReceiptId) {
        val documentRef = getReceiptDocumentRef(userId = userId, receiptId = id)

        if (documentRef.get().await().exists()) {
            documentRef.delete().await()
        }
    }

    private fun getReceiptsCollectionRef(userId: String): CollectionReference {
        return Firebase.firestore.collection("users")
            .document(userId)
            .collection("receipts")
    }

    private fun getReceiptDocumentRef(userId: String, receiptId: ReceiptId): DocumentReference {
        return getReceiptsCollectionRef(userId = userId).document(receiptId)
    }
}
