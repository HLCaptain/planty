package nest.planty.data.firestore.datasource

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import nest.planty.data.firestore.model.FirestoreSensor
import nest.planty.data.network.SensorNetworkDataSource
import org.koin.core.annotation.Single

@Single
class SensorFirestoreDataSource(
    private val firestore: FirebaseFirestore,
) : SensorNetworkDataSource {
    override fun fetch(uuid: String): Flow<FirestoreSensor> {
        Napier.d("Fetching sensor $uuid")
        return firestore
            .collection(FirestoreSensor.COLLECTION_NAME)
            .document(uuid)
            .snapshots()
            .filter { it.exists }
            .map {
                val data = it.data(FirestoreSensor.serializer())
                Napier.d("Fetched sensor $data")
                data
            }
            .catch { Napier.e("Error fetching sensor $uuid", it) }
    }

    override suspend fun upsert(sensor: FirestoreSensor) {
        Napier.d("Upserting sensor $sensor")
        firestore.collection(FirestoreSensor.COLLECTION_NAME)
            .document(sensor.uuid)
            .set(sensor)
    }

    override fun fetchByBroker(brokerUUID: String): Flow<List<FirestoreSensor>> {
        Napier.d("Fetching sensors for broker $brokerUUID")
        return firestore
            .collection(FirestoreSensor.COLLECTION_NAME)
            .where(FirestoreSensor.FIELD_OWNER_BROKER, equalTo = brokerUUID)
            .snapshots()
            .map { snapshot -> snapshot.documents.map {
                val data = it.data(FirestoreSensor.serializer())
                Napier.d("Fetched sensor $data")
                data
            } }
            .catch { Napier.e("Error fetching sensors for broker $brokerUUID", it) }
    }

    override suspend fun delete(uuid: String) {
        Napier.d("Deleting sensor $uuid")
        firestore.collection(FirestoreSensor.COLLECTION_NAME)
            .document(uuid)
            .delete()
    }
}