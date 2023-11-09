package nest.planty.data.firestore.model

import kotlinx.serialization.Serializable

@Serializable
data class FirestorePairingBroker(
    val id: String,
    val pairingStarted: Long,
)
