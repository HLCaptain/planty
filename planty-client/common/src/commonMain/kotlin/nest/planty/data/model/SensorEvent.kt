package nest.planty.data.model

data class SensorEvent(
    val type: String,
    val timestamp: Long,
    val value: String,
)
