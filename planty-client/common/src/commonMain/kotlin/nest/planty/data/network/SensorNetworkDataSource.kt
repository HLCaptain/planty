package nest.planty.data.network

import kotlinx.coroutines.flow.Flow
import nest.planty.data.firestore.model.FirestoreSensor

interface SensorNetworkDataSource {
    fun fetchByBroker(brokerUUID: String): Flow<List<FirestoreSensor>>
    fun fetch(uuid: String): Flow<FirestoreSensor>
    suspend fun upsert(sensor: FirestoreSensor)
    suspend fun delete(uuid: String)
}