package nest.planty.data.firestore.datasource

import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import nest.planty.data.firestore.model.FirestorePairingBroker
import nest.planty.data.network.PairingBrokerNetworkDataSource
import org.koin.core.annotation.Single

@Single
class PairingBrokerFirestoreDataSource(
    private val firestore: FirebaseFirestore,
) : PairingBrokerNetworkDataSource {

    override fun fetchAll(): Flow<List<FirestorePairingBroker>> {
        Napier.d("Fetching all pairing brokers")
        return firestore.collection(FirestorePairingBroker.COLLECTION_NAME)
            .snapshots()
            .map { snapshot -> snapshot.documents.map { it.data(FirestorePairingBroker.serializer()) } }
            .catch { Napier.e("Error fetching all pairing brokers", it) }
    }

    override suspend fun delete(uuid: String) {
        Napier.d("Deleting pairing broker $uuid")
        firestore.collection(FirestorePairingBroker.COLLECTION_NAME)
            .document(uuid)
            .delete()
    }
}