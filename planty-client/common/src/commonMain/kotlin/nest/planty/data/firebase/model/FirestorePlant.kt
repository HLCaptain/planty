package nest.planty.data.firebase.model

import dev.gitlive.firebase.firestore.DocumentReference
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirestorePlant(
    val reference: DocumentReference,
    @SerialName(FIELD_UUID) val uuid: String,
    @SerialName(FIELD_NAME) val name: String,
) {
    companion object {
        const val COLLECTION_NAME = "plants"
        const val FIELD_UUID = "uuid"
        const val FIELD_NAME = "name"
    }
}