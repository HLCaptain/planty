package nest.planty.domain.model

import nest.planty.data.model.SensorEvent

data class DomainPlant(
    val uuid: String,
    val ownerUUID: String,
    val name: String?,
    val description: String?,
    val desiredEnvironment: Map<String, String>,
    val sensorEvents: List<SensorEvent>,
    val sensors: List<String>,
    val brokers: List<String>,
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
            brokers = emptyList(),
            image = null,
        )
    }
}
