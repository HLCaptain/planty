package nest.planty.data.firestore.datasource

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import nest.planty.data.firestore.model.FirestorePlant
import nest.planty.data.network.PlantNetworkDataSource
import org.koin.core.annotation.Factory

@Factory
class PlantFirestoreDataSource(
    private val firestore: FirebaseFirestore,
) : PlantNetworkDataSource {

    override fun fetchByUser(userUUID: String): Flow<List<FirestorePlant>> = flow {
        val plants = mutableListOf<FirestorePlant>()
        firestore
            .collection(FirestorePlant.COLLECTION_NAME)
            .where(FirestorePlant.FIELD_OWNER_UUID, equalTo = userUUID)
            .snapshots()
            .map { snapshot ->
                plants.clear()
                snapshot.documents.forEach { plants.add(it.data()) }
                emit(plants)
            }
    }

    override suspend fun upsert(plant: FirestorePlant) {
        firestore.collection(FirestorePlant.COLLECTION_NAME)
            .document(plant.uuid)
            .set(plant)
    }

    override suspend fun delete(plant: FirestorePlant) {
        firestore.collection(FirestorePlant.COLLECTION_NAME)
            .document(plant.uuid)
            .delete()
    }

    override suspend fun deleteAll(userUUID: String) {
        firestore.collection(FirestorePlant.COLLECTION_NAME)
            .where(FirestorePlant.FIELD_OWNER_UUID, equalTo = userUUID)
            .get()
            .apply { documents.forEach { it.reference.delete() } }
    }

    override fun fetch(uuid: String): Flow<FirestorePlant> = flow {
        firestore.collection(FirestorePlant.COLLECTION_NAME)
            .document(uuid)
            .snapshots()
            .map { snapshot -> emit(snapshot.data()) }
    }

    override suspend fun delete(uuid: String) {
        firestore.collection(FirestorePlant.COLLECTION_NAME)
            .document(uuid)
            .delete()
    }
}