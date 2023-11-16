package nest.planty.domain.model

data class DomainSensorEvent(
    val type: String,
    val timestamp: Long,
    val value: String,
)
