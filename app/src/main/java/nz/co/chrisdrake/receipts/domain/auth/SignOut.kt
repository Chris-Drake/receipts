package nz.co.chrisdrake.receipts.domain.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import nz.co.chrisdrake.receipts.data.UserPreferencesRepository

class SignOut(
    private val userPreferencesRepository: UserPreferencesRepository,
) {

    suspend operator fun invoke() {
        Firebase.auth.signOut()
        userPreferencesRepository.saveLastSyncTime(0L)
    }
}