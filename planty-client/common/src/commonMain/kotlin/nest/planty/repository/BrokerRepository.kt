package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nest.planty.data.store.BrokerMutableStoreBuilder
import nest.planty.data.store.BrokersMutableStoreBuilder
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.domain.model.DomainBroker
import org.koin.core.annotation.Factory
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest

@Factory
class BrokerRepository(
    brokerMutableStoreBuilder: BrokerMutableStoreBuilder,
    brokersMutableStoreBuilder: BrokersMutableStoreBuilder,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher
) {
    @OptIn(ExperimentalStoreApi::class)
    private val brokerMutableStore = brokerMutableStoreBuilder.store
    @OptIn(ExperimentalStoreApi::class)
    private val brokersMutableStore = brokersMutableStoreBuilder.store

    @OptIn(ExperimentalStoreApi::class)
    fun getBroker(uuid: String) = brokerMutableStore.stream<StoreReadResponse<DomainBroker>>(
        StoreReadRequest.cached(
            key = uuid,
            refresh = true
        )
    ).map {
        it.throwIfError()
        Napier.d("Read Response: $it")
        val data = it.dataOrNull()
        Napier.d("Broker is $data")
        data
    }.flowOn(dispatcherIO)

    @OptIn(ExperimentalStoreApi::class)
    fun getBrokersByUser(userUUID: String) =
        brokersMutableStore.stream<StoreReadResponse<List<DomainBroker>>>(
            StoreReadRequest.cached(
                key = userUUID,
                refresh = true
            )
        ).map {
            it.throwIfError()
            Napier.d("Read Response: $it")
            val data = it.dataOrNull()
            Napier.d("Broker is $data")
            data
        }.flowOn(dispatcherIO)

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