package nest.planty.manager

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.domain.model.DomainSensor
import nest.planty.repository.BrokerRepository
import nest.planty.repository.PlantRepository
import nest.planty.repository.SensorRepository
import org.koin.core.annotation.Factory

@Factory
class SensorManager(
    authManager: AuthManager,
    private val sensorRepository: SensorRepository,
    private val brokerRepository: BrokerRepository,
    private val plantManager: PlantManager,
    private val plantRepository: PlantRepository,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) {
    fun getSensorsForUser(userUUID: String) = channelFlow {
        brokerRepository.getBrokersByUser(userUUID).collect { brokers ->
            val sensorMap = mutableMapOf<String, List<DomainSensor>>()
            brokers?.forEach { broker ->
                sensorRepository.getSensorsByBroker(broker.uuid).collect { sensors ->
                    sensors?.let { list ->
                        sensorMap[broker.uuid] = list
                        send(sensorMap.values.flatten().distinctBy { it.uuid })
                    }
                }
            }
        }
    }

    fun getSensorsForPlant(plantUUID: String) = plantManager
        .getPlant(plantUUID)
        .map { it?.sensors }

    suspend fun unassignSensorFromPlant(sensorUUID: String, plantUUID: String) {
        plantRepository.getPlant(plantUUID).firstOrNull()?.let { plant ->
            plantRepository.upsertPlantForUser(
                plant.copy(sensors = plant.sensors.filterNot { it == sensorUUID })
            )
        }
    }

    suspend fun assignSensorToPlant(sensorUUID: String, plantUUID: String) {
        plantRepository.getPlant(plantUUID).firstOrNull()?.let { plant ->
            plantRepository.upsertPlantForUser(
                plant.copy(sensors = (plant.sensors + sensorUUID).distinct())
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val availableSensors = authManager.signedInUser.flatMapLatest { user ->
        user?.uid?.let { getSensorsForUser(it) } ?: flowOf(emptyList())
    }.flowOn(dispatcherIO)
}