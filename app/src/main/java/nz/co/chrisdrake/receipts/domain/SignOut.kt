package nz.co.chrisdrake.receipts.domain

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignOut {

    operator fun invoke() {
        Firebase.auth.signOut()
    }
}