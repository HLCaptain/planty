package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nest.planty.data.store.BrokerMutableStoreBuilder
import nest.planty.data.store.BrokersMutableStoreBuilder
import nest.planty.di.NamedCoroutineScopeIO
import nest.planty.domain.model.DomainBroker
import org.koin.core.annotation.Single
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest

@Single
class BrokerRepository(
    brokerMutableStoreBuilder: BrokerMutableStoreBuilder,
    brokersMutableStoreBuilder: BrokersMutableStoreBuilder,
    @NamedCoroutineScopeIO private val coroutineScopeIO: CoroutineScope,
) {
    @OptIn(ExperimentalStoreApi::class)
    private val brokerMutableStore = brokerMutableStoreBuilder.store
    @OptIn(ExperimentalStoreApi::class)
    private val brokersMutableStore = brokersMutableStoreBuilder.store

    private val brokerStateFlows = mutableMapOf<String, StateFlow<Pair<DomainBroker?, Boolean>>>()
    @OptIn(ExperimentalStoreApi::class)
    fun getBroker(uuid: String): StateFlow<Pair<DomainBroker?, Boolean>> {
        return brokerStateFlows.getOrPut(uuid) {
            brokerMutableStore.stream<StoreReadResponse<DomainBroker>>(
                StoreReadRequest.fresh(key = uuid)
            ).map {
                it.throwIfError()
                Napier.d("Read Response: $it")
                val data = it.dataOrNull()
                Napier.d("Broker is $data")
                data to (it is StoreReadResponse.Loading)
            }.stateIn(
                coroutineScopeIO,
                SharingStarted.Eagerly,
                null to true
            )
        }
    }

    private val brokersStateFlows = mutableMapOf<String, StateFlow<Pair<List<DomainBroker>?, Boolean>>>()
    @OptIn(ExperimentalStoreApi::class)
    fun getBrokersByUser(userUUID: String): StateFlow<Pair<List<DomainBroker>?, Boolean>> {
        return brokersStateFlows.getOrPut(userUUID) {
            brokersMutableStore.stream<StoreReadResponse<List<DomainBroker>>>(
                StoreReadRequest.fresh(key = userUUID)
            ).map {
                it.throwIfError()
                Napier.d("Read Response: $it")
                val data = it.dataOrNull()
                Napier.d("Broker is $data")
                data to (it is StoreReadResponse.Loading)
            }.stateIn(
                coroutineScopeIO,
                SharingStarted.Eagerly,
                null to true
            )
        }
    }

    @OptIn(ExperimentalStoreApi::class)
    suspend fun upsertBroker(broker: DomainBroker) {
        brokerMutableStore.write(
            StoreWriteRequest.of(
                key = broker.uuid,
                value = broker
            )
        )
    }
}