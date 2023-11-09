package nest.planty.data.network

import kotlinx.coroutines.flow.Flow
import nest.planty.data.firestore.model.FirestorePlant

interface PlantNetworkDataSource {
    fun fetchByUser(userUUID: String): Flow<List<FirestorePlant>>
    fun fetch(uuid: String): Flow<FirestorePlant>
    suspend fun upsert(plant: FirestorePlant)
    suspend fun delete(plant: FirestorePlant)
    suspend fun deleteAll(userUUID: String)
    suspend fun delete(uuid: String)
}
