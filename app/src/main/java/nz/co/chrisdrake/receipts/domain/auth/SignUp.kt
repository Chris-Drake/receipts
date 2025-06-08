package nz.co.chrisdrake.receipts.domain.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class SignUp {

    suspend operator fun invoke(email: String, password: String) {
        Firebase.auth
            .createUserWithEmailAndPassword(email, password)
            .await()
    }
}