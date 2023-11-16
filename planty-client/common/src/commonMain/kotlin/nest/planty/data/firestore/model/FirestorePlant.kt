package nest.planty.data.firestore.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirestorePlant(
    @SerialName(FIELD_UUID) val uuid: String,
    val ownerUUID: String,
    @SerialName(FIELD_NAME) val name: String?,
    val description: String?,
    val desiredEnvironment: Map<String, String>,
    val sensorEvents: List<FirestoreSensorEvent>,
    val sensors: List<String>,
    val image: String?,
) {
    companion object {
        const val COLLECTION_NAME = "plants"
        const val FIELD_UUID = "uuid"
        const val FIELD_NAME = "name"
        const val FIELD_OWNER_UUID = "ownerUUID"
        const val FIELD_SENSOR_EVENTS = "sensorEvents"
        const val FIELD_SENSORS = "sensors"
        const val FIELD_IMAGE = "image"
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_DESIRED_ENVIRONMENT = "desiredEnvironment"
    }
}