package nest.planty.data.firestore.model

import kotlinx.serialization.Serializable

@Serializable
data class FirestorePairingBroker(
    val uuid: String,
    val pairingStarted: Long,
) {
    companion object {
        const val COLLECTION_NAME = "pairing"
        const val FIELD_UUID = "uuid"
        const val FIELD_PAIRING_STARTED = "pairingStarted"
    }
}
