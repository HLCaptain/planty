package nest.planty.manager

import kotlinx.coroutines.flow.map
import nest.planty.data.mapping.toDomainModel
import nest.planty.repository.UserRepository
import org.koin.core.annotation.Single

@Single
class AuthManager(
    private val userRepository: UserRepository
) {
    val signedInUser = userRepository.signedInUser.map { it?.toDomainModel() }
    val isUserSignedIn = signedInUser.map { it != null }

    suspend fun signInAnonymously() = userRepository.anonymousSignIn()
    suspend fun signOut() = userRepository.signOut()
}