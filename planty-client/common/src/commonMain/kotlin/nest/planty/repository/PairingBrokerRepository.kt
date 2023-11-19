package nest.planty.repository

import nest.planty.data.network.PairingBrokerNetworkDataSource
import org.koin.core.annotation.Single

@Single
class PairingBrokerRepository(
    private val pairingBrokerNetworkDataSource: PairingBrokerNetworkDataSource,
) {
    fun getPairingBrokers() = pairingBrokerNetworkDataSource.fetchAll()

    suspend fun deletePairingBroker(brokerUUID: String) =
        pairingBrokerNetworkDataSource.delete(brokerUUID)
}