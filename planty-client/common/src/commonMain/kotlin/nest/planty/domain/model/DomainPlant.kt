package nest.planty.domain.model

data class DomainPlant(
    val uuid: String,
    val ownerUUID: String,
    val name: String?,
    val description: String?,
    val desiredEnvironment: Map<String, String>,
    val sensorEvents: List<DomainSensorEvent>,
    val sensors: List<DomainSensor>,
    val image: String?,
) {
    companion object {
        val Default = DomainPlant(
            uuid = "",
            ownerUUID = "",
            name = null,
            description = null,
            desiredEnvironment = emptyMap(),
            sensorEvents = emptyList(),
            sensors = emptyList(),
            image = null,
        )
    }
}
