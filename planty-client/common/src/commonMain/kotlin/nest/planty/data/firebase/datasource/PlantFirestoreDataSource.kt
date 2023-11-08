package nest.planty.data.firebase.datasource

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import nest.planty.data.firebase.model.FirestorePlant
import nest.planty.data.network.PlantNetworkDataSource
import org.koin.core.annotation.Factory

@Factory
class PlantFirestoreDataSource(
    private val firestore: FirebaseFirestore
) : PlantNetworkDataSource {

    override fun fetch(uuid: String): Flow<List<FirestorePlant>> {
        TODO("Not yet implemented")
    }

    override fun upsert(plant: FirestorePlant) {
        TODO("Not yet implemented")
    }

    override fun delete(plant: FirestorePlant) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }
}