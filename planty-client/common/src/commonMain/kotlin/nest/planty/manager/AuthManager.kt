package nest.planty.manager

import kotlinx.coroutines.flow.map
import nest.planty.repository.UserRepository
import org.koin.core.annotation.Single

@Single
class AuthManager(
    private val userRepository: UserRepository
) {
    val signedInUser = userRepository.getSignedInUserFlow()
    val isUserSignedIn = signedInUser.map { it != null }

    suspend fun signInAnonymously() = userRepository.anonymousSignIn()
    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ) = userRepository.signInWithEmailAndPassword(email, password)

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ) = userRepository.createUserWithEmailAndPassword(email, password)

    suspend fun sendPasswordResetEmail(email: String) = userRepository.sendPasswordResetEmail(email)
    suspend fun sendPasswordResetEmailToCurrentUser() = userRepository.sendPasswordResetEmailToCurrentUser()
    suspend fun signOut() = userRepository.signOut()
}