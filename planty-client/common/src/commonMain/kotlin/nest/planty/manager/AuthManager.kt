package nest.planty.manager

import nest.planty.repository.UserRepository
import org.koin.core.annotation.Single

@Single
class AuthManager(
    userRepository: UserRepository
) {
}