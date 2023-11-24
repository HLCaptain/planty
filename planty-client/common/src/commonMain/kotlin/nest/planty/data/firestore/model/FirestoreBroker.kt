package nest.planty.data.firestore.model

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreBroker(
    val uuid: String,
    val ownerUUID: String? = null,
    val sensors: List<String> = emptyList()
) {
    companion object {
        const val COLLECTION_NAME = "brokers"
        const val FIELD_OWNER_UUID = "ownerUUID"
    }
}
