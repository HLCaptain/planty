package nest.planty.data.firestore.model

import kotlinx.serialization.Serializable

@Serializable
data class FirestorePairingBroker(
    val uuid: String,
    val pairingStarted: Long,
)
