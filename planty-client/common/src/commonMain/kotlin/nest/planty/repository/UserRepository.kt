package nest.planty.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import nest.planty.di.NamedCoroutineScopeIO
import org.koin.core.annotation.Single

@Single
class UserRepository(
    private val auth: FirebaseAuth,
    @NamedCoroutineScopeIO private val coroutineScopeIO: CoroutineScope,
) {
    val signedInUser = auth.authStateChanged.stateIn(
        scope = coroutineScopeIO,
        started = SharingStarted.Eagerly,
        initialValue = auth.currentUser
    )

    suspend fun anonymousSignIn() {
        auth.signInAnonymously()
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    suspend fun sendPasswordResetEmailToCurrentUser() {
        auth.currentUser?.email?.let { email ->
            auth.sendPasswordResetEmail(email)
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun delete() {
        auth.currentUser?.delete()
    }
}