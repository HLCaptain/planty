package nest.planty.domain.model

data class DomainSensor(
    val uuid: String,
    val ownerBroker: String,
    val type: String,
)
