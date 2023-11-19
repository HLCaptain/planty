package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.map
import nest.planty.data.store.BrokerMutableStoreBuilder
import nest.planty.data.store.BrokersMutableStoreBuilder
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
) {
    @OptIn(ExperimentalStoreApi::class)
    private val brokerMutableStore = brokerMutableStoreBuilder.store
    @OptIn(ExperimentalStoreApi::class)
    private val brokersMutableStore = brokersMutableStoreBuilder.store

    @OptIn(ExperimentalStoreApi::class)
    fun getBroker(uuid: String) = brokerMutableStore.stream<StoreReadResponse<DomainBroker>>(
        StoreReadRequest.fresh(key = uuid)
    ).dropWhile {
        it is StoreReadResponse.Loading
    }.map {
        it.throwIfError()
        Napier.d("Read Response: $it")
        val data = it.dataOrNull()
        Napier.d("Broker is $data")
        data
    }

    @OptIn(ExperimentalStoreApi::class)
    fun getBrokersByUser(userUUID: String) =
        brokersMutableStore.stream<StoreReadResponse<List<DomainBroker>>>(
            StoreReadRequest.fresh(key = userUUID)
        ).dropWhile {
            it is StoreReadResponse.Loading
        }.map {
            it.throwIfError()
            Napier.d("Read Response: $it")
            val data = it.dataOrNull()
            Napier.d("Broker is $data")
            data
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