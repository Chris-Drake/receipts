package nz.co.chrisdrake.receipts.domain.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nz.co.chrisdrake.receipts.domain.PerformSync

class SignIn(
    private val performSync: PerformSync,
) {

    suspend operator fun invoke(email: String, password: String) {
        Firebase.auth
            .signInWithEmailAndPassword(email, password)
            .await()

        performSync()
    }
}