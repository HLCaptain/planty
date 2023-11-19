package nest.planty.manager

import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import nest.planty.repository.BrokerRepository
import nest.planty.repository.PairingBrokerRepository
import org.koin.core.annotation.Single

@Single
class BrokerManager(
    private val pairingBrokerRepository: PairingBrokerRepository,
    private val brokerRepository: BrokerRepository,
    private val authManager: AuthManager,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val pairingBrokers = pairingBrokerRepository.getPairingBrokers()
        .flatMapLatest { pairingBrokers ->
            return@flatMapLatest combine(
                pairingBrokers.map { pairingBroker ->
                    brokerRepository.getBroker(pairingBroker.uuid).dropWhile { it.second }.map { it.first }
                } + flowOf(null) // Add null to the list to avoid empty flow
            ) { it.filterNotNull() }
        }

    suspend fun ownBroker(brokerUUID: String) {
        authManager.signedInUser.map { it?.uid }.first()?.let { userUUID ->
            brokerRepository.getBroker(brokerUUID).first { !it.second }.let { (brokerToOwn, _) ->
                brokerToOwn?.copy(ownerUUID = userUUID)?.let { brokerRepository.upsertBroker(it) }
                Napier.d("User $userUUID owns broker $brokerUUID")
                pairingBrokerRepository.deletePairingBroker(brokerUUID)
                Napier.d("Broker $brokerUUID is not pairing anymore")
            }
        }
    }
}