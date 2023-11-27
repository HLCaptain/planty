package nest.planty.manager

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nest.planty.data.mapping.toDomainModel
import nest.planty.db.Plant
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.repository.PlantRepository
import nest.planty.repository.SensorRepository
import nest.planty.util.log.randomUUID
import org.koin.core.annotation.Single

@Single
class PlantManager(
    private val authManager: AuthManager,
    private val plantRepository: PlantRepository,
    private val sensorRepository: SensorRepository,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) {

    fun getPlant(uuid: String) = channelFlow {
        Napier.d("Getting plant $uuid")
        plantRepository.getPlant(uuid).collect { (plant, _) ->
            Napier.d("Plant is $plant")
            val sensors = plant?.sensors?.map { sensorUUID ->
                sensorRepository.getSensor(sensorUUID).first { !it.second }.first
            } ?: emptyList()
            send(plant?.toDomainModel(sensors.filterNotNull()))
        }
    }

    suspend fun addPlant(
        plantName: String? = null,
        plantDescription: String? = null,
        plantDesiredEnvironment: Map<String, String> = emptyMap(),
        plantSensors: List<String> = emptyList(),
        plantImage: String? = null,
    ) {
        val user = authManager.signedInUser.firstOrNull() ?: return
        Napier.d("Adding plant for user")
        plantRepository.upsertPlant(
            Plant(
                uuid = randomUUID(),
                ownerUUID = user.uid,
                name = plantName,
                description = plantDescription,
                desiredEnvironment = plantDesiredEnvironment,
                sensorEvents = emptyList(),
                sensors = plantSensors,
                image = plantImage,
            )
        )
    }

    suspend fun deletePlant(uuid: String) {
        plantRepository.deletePlant(uuid)
    }

    suspend fun deletePlantsForUser() {
        authManager.signedInUser.firstOrNull()?.let {
            plantRepository.deletePlantsForUser(it.uid)
        }
    }

    suspend fun setDesiredEnvironmentVariable(
        plantUUID: String,
        name: String,
        value: Double,
    ) {
        plantRepository.getPlant(plantUUID)
            .filterNot { it.second }
            .map { it.first }.firstOrNull()
            ?.let { plant ->
                plantRepository.upsertPlant(
                    plant.copy(
                        desiredEnvironment = plant.desiredEnvironment + (name to value.toString())
                    )
                )
            }
    }

    suspend fun setDesiredEnvironmentVariableMap(
        plantUUID: String,
        floatDesiredEnvironmentVariableMap: Map<String, Double>,
    ) {
        Napier.d("Setting desired environment variable map $floatDesiredEnvironmentVariableMap")
        if (floatDesiredEnvironmentVariableMap.isEmpty()) {
            Napier.d("Empty map, returning")
            return
        }
        plantRepository.getPlant(plantUUID)
            .filterNot { it.second }
            .map { it.first }.firstOrNull()
            ?.let { plant ->
                plantRepository.upsertPlant(
                    plant.copy(
                        desiredEnvironment = plant.desiredEnvironment + floatDesiredEnvironmentVariableMap.mapValues { it.value.toString() }
                    )
                )
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val plantsByUser = authManager.signedInUser.flatMapLatest { user ->
        user?.uid?.let { plants ->
            plantRepository.getPlantsByUser(plants).map { it.first }
        } ?: flowOf(emptyList())
    }.flowOn(dispatcherIO)
}