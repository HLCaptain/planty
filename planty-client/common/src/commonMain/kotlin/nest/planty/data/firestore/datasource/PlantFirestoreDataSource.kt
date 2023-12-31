package nest.planty.data.firestore.datasource

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import nest.planty.data.firestore.model.FirestorePlant
import nest.planty.data.network.PlantNetworkDataSource
import org.koin.core.annotation.Single

@Single
class PlantFirestoreDataSource(
    private val firestore: FirebaseFirestore,
) : PlantNetworkDataSource {

    override fun fetchByUser(userUUID: String): Flow<List<FirestorePlant>> {
        Napier.d("Fetching plants for user $userUUID")
        return firestore
            .collection(FirestorePlant.COLLECTION_NAME)
            .where(FirestorePlant.FIELD_OWNER_UUID, equalTo = userUUID)
            .snapshots()
            .map { snapshot -> snapshot.documents.map {
                val data = it.data(FirestorePlant.serializer())
                Napier.d("Fetched plant $data")
                data
            } }
            .catch { Napier.e("Error fetching plants for user $userUUID", it) }
    }

    override suspend fun upsert(plant: FirestorePlant) {
        Napier.d("Upserting plant $plant")
        firestore.collection(FirestorePlant.COLLECTION_NAME)
            .document(plant.uuid)
            .set(plant)
    }

    override suspend fun delete(plant: FirestorePlant): Flow<Unit> {
        Napier.d("Deleting plant $plant")
        return firestore.collection(FirestorePlant.COLLECTION_NAME)
            .where(FirestorePlant.FIELD_UUID, equalTo = plant.uuid)
            .snapshots()
            .map { snapshot ->
                Napier.d("Deleting plant $plant for real")
                snapshot.documents.forEach { it.reference.delete() }
            }
    }

    override suspend fun deleteAll(userUUID: String) {
        Napier.d("Deleting all plants for user $userUUID")
        firestore.collection(FirestorePlant.COLLECTION_NAME)
            .where(FirestorePlant.FIELD_OWNER_UUID, equalTo = userUUID)
            .get()
            .apply { documents.forEach { it.reference.delete() } }
    }

    override fun fetch(uuid: String): Flow<FirestorePlant> {
        Napier.d("Fetching plant $uuid")
        return firestore.collection(FirestorePlant.COLLECTION_NAME)
            .document(uuid)
            .snapshots()
            .filter { it.exists }
            .map {
                val data = it.data(FirestorePlant.serializer())
                Napier.d("Fetched plant $data")
                data
            }
            .catch { Napier.e("Error fetching plant $uuid", it) }
    }

    override suspend fun delete(uuid: String) {
        Napier.d("Deleting plant $uuid")
        firestore.collection(FirestorePlant.COLLECTION_NAME)
            .document(uuid)
            .delete()
    }
}