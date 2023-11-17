package nest.planty.data.network

import kotlinx.coroutines.flow.Flow
import nest.planty.data.firestore.model.FirestorePairingBroker

interface PairingBrokerNetworkDataSource {
    fun fetchAll(): Flow<List<FirestorePairingBroker>>
    suspend fun delete(uuid: String)
}