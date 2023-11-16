package nest.planty.domain.model

data class DomainBroker(
    val uuid: String,
    val ownerUUID: String,
    val sensors: List<String>,
)
