package nz.co.chrisdrake.receipts.domain

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class SignIn {

    suspend operator fun invoke(email: String, password: String) {
        Firebase.auth
            .signInWithEmailAndPassword(email, password)
            .await()
    }
}