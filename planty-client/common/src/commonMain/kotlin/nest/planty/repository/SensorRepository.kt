package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nest.planty.data.store.SensorMutableStoreBuilder
import nest.planty.data.store.SensorsMutableStoreBuilder
import nest.planty.di.NamedCoroutineScopeIO
import nest.planty.domain.model.DomainSensor
import org.koin.core.annotation.Single
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

@Single
class SensorRepository(
    sensorMutableStoreBuilder: SensorMutableStoreBuilder,
    sensorsMutableStoreBuilder: SensorsMutableStoreBuilder,
    @NamedCoroutineScopeIO private val coroutineScopeIO: CoroutineScope,
) {
    @OptIn(ExperimentalStoreApi::class)
    private val sensorMutableStore = sensorMutableStoreBuilder.store
    @OptIn(ExperimentalStoreApi::class)
    private val sensorsMutableStore = sensorsMutableStoreBuilder.store

    private val sensorStateFlows = mutableMapOf<String, StateFlow<Pair<DomainSensor?, Boolean>>>()
    @OptIn(ExperimentalStoreApi::class)
    fun getSensor(uuid: String): StateFlow<Pair<DomainSensor?, Boolean>> {
        return sensorStateFlows.getOrPut(uuid) {
            sensorMutableStore.stream<StoreReadResponse<DomainSensor>>(
                StoreReadRequest.fresh(key = uuid)
            ).map {
                it.throwIfError()
                Napier.d("Read Response: $it")
                val data = it.dataOrNull()
                Napier.d("Sensor is $data")
                data to (it is StoreReadResponse.Loading)
            }.stateIn(
                coroutineScopeIO,
                SharingStarted.Eagerly,
                null to true
            )
        }
    }

    private val sensorsStateFlows = mutableMapOf<String, StateFlow<Pair<List<DomainSensor>?, Boolean>>>()
    @OptIn(ExperimentalStoreApi::class)
    fun getSensorsByBroker(brokerUUID: String): StateFlow<Pair<List<DomainSensor>?, Boolean>> {
        return sensorsStateFlows.getOrPut(brokerUUID) {
            sensorsMutableStore.stream<StoreReadResponse<List<DomainSensor>>>(
                StoreReadRequest.fresh(key = brokerUUID)
            ).dropWhile {
                it is StoreReadResponse.Loading
            }.map {
                it.throwIfError()
                Napier.d("Read Response: $it")
                val data = it.dataOrNull()
                Napier.d("Sensor is $data")
                data?.sortedBy { sensor -> sensor.type } to (it is StoreReadResponse.Loading)
            }.stateIn(
                coroutineScopeIO,
                SharingStarted.Eagerly,
                null to true
            )
        }
    }
}