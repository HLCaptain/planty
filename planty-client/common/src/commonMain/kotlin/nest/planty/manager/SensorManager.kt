package nest.planty.manager

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
    private val plantRepository: PlantRepository,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) {
    fun getSensorsForUser(userUUID: String) = flow {
        brokerRepository.getBrokersByUser(userUUID).collect { brokers ->
            val sensorMap = mutableMapOf<String, List<DomainSensor>>()
            brokers?.forEach { broker ->
                sensorRepository.getSensorsByBroker(broker.uuid).collect { sensors ->
                    sensors?.let { list ->
                        sensorMap[broker.uuid] = list
                        emit(sensorMap.values.flatten().distinctBy { it.uuid })
                    }
                }
            }
        }
    }

    fun getSensorsForPlant(plantUUID: String) = flow {
        plantRepository.getPlant(plantUUID).collect { plant ->
            val sensorMap = mutableMapOf<String, List<DomainSensor>>()
            plant?.sensors?.forEach { sensorUUID ->
                sensorRepository.getSensor(sensorUUID).collect { sensor ->
                    sensor?.let { list ->
                        sensorMap[sensorUUID] = listOf(list)
                        emit(sensorMap.values.flatten().distinctBy { it.uuid })
                    }
                }
            }
        }
    }

    suspend fun assignSensorToPlant(sensorUUID: String, plantUUID: String) {
        plantRepository.getPlant(plantUUID).firstOrNull()?.let { plant ->
            plantRepository.upsertPlantForUser(
                plant.copy(sensors = plant.sensors.filterNot { it == sensorUUID })
            )
        }
    }

    suspend fun unassignSensorFromPlant(sensorUUID: String, plantUUID: String) {
        plantRepository.getPlant(plantUUID).firstOrNull()?.let { plant ->
            plantRepository.upsertPlantForUser(
                plant.copy(sensors = (plant.sensors + sensorUUID).distinct())
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val availableSensors = authManager.signedInUser.flatMapLatest { user ->
        user?.uid?.let { getSensorsForUser(it) } ?: emptyFlow()
    }.flowOn(dispatcherIO)
}