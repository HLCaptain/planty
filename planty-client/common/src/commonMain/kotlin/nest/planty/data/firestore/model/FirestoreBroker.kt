package nest.planty.data.firestore.model

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreBroker(
    val id: String,
    val ownerUUID: String,
    val sensors: List<String>,
)
