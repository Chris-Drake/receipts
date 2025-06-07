package nz.co.chrisdrake.receipts.data

import android.net.Uri
import androidx.core.net.toFile
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import nz.co.chrisdrake.receipts.domain.Receipt
import nz.co.chrisdrake.receipts.domain.ReceiptId

class RemoteDataSource {

    suspend fun saveReceipt(userId: String, receipt: Receipt) {
        val imagePath = storeImage(
            userId = userId,
            fileUri = receipt.imageUri,
        )

        getReceiptDocumentRef(userId = userId, receiptId = receipt.id)
            .set(receipt.toRemoteEntity(imagePath = imagePath))
            .await()
    }

    private suspend fun storeImage(userId: String, fileUri: Uri): String {
        val imageFile = fileUri.toFile()
        val imageRef = Firebase.storage.reference
            .child("/receipts/${userId}/${imageFile.name}")

        imageRef.putFile(fileUri).await()

        return imageRef.path
    }

    suspend fun deleteReceipt(userId: String, id: ReceiptId) {
        val documentRef = getReceiptDocumentRef(userId = userId, receiptId = id)

        if (documentRef.get().await().exists()) {
            documentRef.delete().await()
        }
    }

    private fun getReceiptDocumentRef(userId: String, receiptId: ReceiptId): DocumentReference {
        return Firebase.firestore.collection("users")
            .document(userId)
            .collection("receipts")
            .document(receiptId)
    }
}