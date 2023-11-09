package nest.planty.data.firestore.model

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreSensor(
    val id: String,
    val ownerBroker: String,
    val type: String,
)
