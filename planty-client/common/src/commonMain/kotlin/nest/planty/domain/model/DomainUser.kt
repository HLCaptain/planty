package nest.planty.domain.model

class DomainUser(val uuid: String) {
    companion object {
        private const val LocalUserUUID = "local-user-uuid"
        val LocalUser = DomainUser(uuid = LocalUserUUID)
    }
}