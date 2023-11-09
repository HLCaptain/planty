package nest.planty.data.firestore.model

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreSensorEvent(
    val timestamp: Long,
    val type: String,
    val value: String,
)