package nest.planty.data.firestore.model

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreSensor(
    val uuid: String,
    val ownerBroker: String,
    val type: String,
) {
    companion object {
        const val COLLECTION_NAME = "sensors"
        const val FIELD_OWNER_BROKER = "ownerBroker"
    }
}
