package nz.co.chrisdrake.receipts.domain

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GetCurrentUser {

    operator fun invoke(): User? {
        val currentUser = Firebase.auth.currentUser ?: return null

        return User(
            id = currentUser.uid,
            email = checkNotNull(currentUser.email),
        )
    }
}