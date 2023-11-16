package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nest.planty.data.store.SensorMutableStoreBuilder
import nest.planty.data.store.SensorsMutableStoreBuilder
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.domain.model.DomainSensor
import org.koin.core.annotation.Factory
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

@Factory
class SensorRepository(
    sensorMutableStoreBuilder: SensorMutableStoreBuilder,
    sensorsMutableStoreBuilder: SensorsMutableStoreBuilder,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher
) {
    @OptIn(ExperimentalStoreApi::class)
    private val sensorMutableStore = sensorMutableStoreBuilder.store
    private val sensorsMutableStore = sensorsMutableStoreBuilder.store

    @OptIn(ExperimentalStoreApi::class)
    fun getSensor(uuid: String) = sensorMutableStore.stream<StoreReadResponse<DomainSensor>>(
        StoreReadRequest.cached(
            key = uuid,
            refresh = true
        )
    ).map {
        it.throwIfError()
        Napier.d("Read Response: $it")
        val data = it.dataOrNull()
        Napier.d("Sensor is $data")
        data
    }.flowOn(dispatcherIO)

    @OptIn(ExperimentalStoreApi::class)
    fun getSensorsByBroker(brokerUUID: String) =
        sensorsMutableStore.stream<StoreReadResponse<List<DomainSensor>>>(
            StoreReadRequest.cached(
                key = brokerUUID,
                refresh = true
            )
        ).map {
            it.throwIfError()
            Napier.d("Read Response: $it")
            val data = it.dataOrNull()
            Napier.d("Sensor is $data")
            data?.sortedBy { sensor -> sensor.type }
        }.flowOn(dispatcherIO)
}