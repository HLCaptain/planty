package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.map
import nest.planty.data.store.SensorMutableStoreBuilder
import nest.planty.data.store.SensorsMutableStoreBuilder
import nest.planty.domain.model.DomainSensor
import org.koin.core.annotation.Factory
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

@Factory
class SensorRepository(
    sensorMutableStoreBuilder: SensorMutableStoreBuilder,
    sensorsMutableStoreBuilder: SensorsMutableStoreBuilder,
) {
    @OptIn(ExperimentalStoreApi::class)
    private val sensorMutableStore = sensorMutableStoreBuilder.store
    @OptIn(ExperimentalStoreApi::class)
    private val sensorsMutableStore = sensorsMutableStoreBuilder.store

    @OptIn(ExperimentalStoreApi::class)
    fun getSensor(uuid: String) = sensorMutableStore.stream<StoreReadResponse<DomainSensor>>(
        StoreReadRequest.fresh(key = uuid)
    ).dropWhile {
        it is StoreReadResponse.Loading
    }.map {
        it.throwIfError()
        Napier.d("Read Response: $it")
        val data = it.dataOrNull()
        Napier.d("Sensor is $data")
        data
    }

    @OptIn(ExperimentalStoreApi::class)
    fun getSensorsByBroker(brokerUUID: String) =
        sensorsMutableStore.stream<StoreReadResponse<List<DomainSensor>>>(
            StoreReadRequest.fresh(key = brokerUUID)
        ).dropWhile {
            it is StoreReadResponse.Loading
        }.map {
            it.throwIfError()
            Napier.d("Read Response: $it")
            val data = it.dataOrNull()
            Napier.d("Sensor is $data")
            data?.sortedBy { sensor -> sensor.type }
        }
}