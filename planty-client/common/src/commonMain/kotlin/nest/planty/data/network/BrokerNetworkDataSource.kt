package nest.planty.data.network

import kotlinx.coroutines.flow.Flow
import nest.planty.data.firestore.model.FirestoreBroker

interface BrokerNetworkDataSource {
    fun fetchByUser(userUUID: String): Flow<List<FirestoreBroker>>
    fun fetch(uuid: String): Flow<FirestoreBroker>
    suspend fun upsert(broker: FirestoreBroker)
    suspend fun delete(uuid: String)
    suspend fun deleteAll(userUUID: String)
}