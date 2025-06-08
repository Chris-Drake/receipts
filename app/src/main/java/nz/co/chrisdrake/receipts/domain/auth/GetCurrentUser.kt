package nz.co.chrisdrake.receipts.domain.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import nz.co.chrisdrake.receipts.domain.model.User

class GetCurrentUser {

    operator fun invoke(): User? {
        val currentUser = Firebase.auth.currentUser ?: return null

        return User(
            id = currentUser.uid,
            email = checkNotNull(currentUser.email),
        )
    }
}