package nest.planty.data.network

import kotlinx.coroutines.flow.Flow
import nest.planty.data.firebase.model.FirestorePlant

interface PlantNetworkDataSource {
    fun fetch(uuid: String): Flow<List<FirestorePlant>>
    fun upsert(plant: FirestorePlant)
    fun delete(plant: FirestorePlant)
    fun deleteAll()
}