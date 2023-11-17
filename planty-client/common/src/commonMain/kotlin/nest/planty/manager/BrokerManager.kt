package nest.planty.manager

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.repository.BrokerRepository
import nest.planty.repository.PairingBrokerRepository
import org.koin.core.annotation.Factory

@Factory
class BrokerManager(
    private val pairingBrokerRepository: PairingBrokerRepository,
    private val brokerRepository: BrokerRepository,
    private val authManager: AuthManager,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val pairingBrokers = pairingBrokerRepository.getPairingBrokers()
        .flowOn(dispatcherIO)
        .flatMapLatest { pairingBrokers ->
            return@flatMapLatest combine(
                pairingBrokers.map { pairingBroker ->
                    brokerRepository.getBroker(pairingBroker.uuid).flowOn(dispatcherIO)
                } + flowOf(null) // Add null to the list to avoid empty flow
            ) { it.filterNotNull() }
        }

    suspend fun ownBroker(brokerUUID: String) {
        authManager.signedInUser.map { it?.uid }.first()?.let { userUUID ->
            brokerRepository.getBroker(brokerUUID).first()?.let { brokerToOwn ->
                brokerRepository.upsertBroker(brokerToOwn.copy(ownerUUID = userUUID))
                Napier.d("User $userUUID owns broker $brokerUUID")
                pairingBrokerRepository.deletePairingBroker(brokerUUID)
                Napier.d("Broker $brokerUUID is not pairing anymore")
            }
        }
    }
}