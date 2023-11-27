package nest.planty.data.firestore.datasource

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import nest.planty.data.firestore.model.FirestoreBroker
import nest.planty.data.network.BrokerNetworkDataSource
import org.koin.core.annotation.Single

@Single
class BrokerFirestoreDataSource(
    private val firestore: FirebaseFirestore,
) : BrokerNetworkDataSource {
    override fun fetch(uuid: String): Flow<FirestoreBroker> {
        Napier.d("Fetching broker")
        return firestore
            .collection(FirestoreBroker.COLLECTION_NAME)
            .document(uuid)
            .snapshots()
            .filter { it.exists }
            .map {
                val data = it.data(FirestoreBroker.serializer())
                Napier.d("Fetched broker $data")
                data
            }
            .catch { Napier.e("Error fetching broker $uuid", it) }
    }

    override fun fetchByUser(userUUID: String): Flow<List<FirestoreBroker>> {
        Napier.d("Fetching brokers for user $userUUID")
        return firestore
            .collection(FirestoreBroker.COLLECTION_NAME)
            .where(FirestoreBroker.FIELD_OWNER_UUID, equalTo = userUUID)
            .snapshots()
            .map { snapshot -> snapshot.documents.map {
                val data = it.data(FirestoreBroker.serializer())
                Napier.d("Fetched broker $data")
                data
            } }
            .catch { Napier.e("Error fetching brokers for user $userUUID", it) }
    }

    override suspend fun upsert(broker: FirestoreBroker) {
        Napier.d("Upserting broker $broker")
        firestore.collection(FirestoreBroker.COLLECTION_NAME)
            .document(broker.uuid)
            .set(broker)
    }

    override suspend fun delete(uuid: String) {
        Napier.d("Deleting broker $uuid")
        firestore.collection(FirestoreBroker.COLLECTION_NAME)
            .document(uuid)
            .delete()
    }

    override suspend fun deleteAll(userUUID: String) {
        Napier.d("Deleting all brokers for user $userUUID")
        firestore.collection(FirestoreBroker.COLLECTION_NAME)
            .where(FirestoreBroker.FIELD_OWNER_UUID, equalTo = userUUID)
            .get()
            .apply { documents.forEach { it.reference.delete() } }
    }
}