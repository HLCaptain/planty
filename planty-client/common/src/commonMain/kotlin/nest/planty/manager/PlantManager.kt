package nest.planty.manager

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import nest.planty.data.mapping.toDomainModel
import nest.planty.db.Plant
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.repository.PlantRepository
import nest.planty.repository.SensorRepository
import nest.planty.util.log.randomUUID
import org.koin.core.annotation.Factory

@Factory
class PlantManager(
    private val authManager: AuthManager,
    private val plantRepository: PlantRepository,
    private val sensorRepository: SensorRepository,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) {
    fun getPlant(uuid: String) = channelFlow {
        plantRepository.getPlant(uuid).collect { plant ->
            val sensors = plant?.sensors?.map { sensorUUID ->
                sensorRepository.getSensor(sensorUUID).firstOrNull()
            } ?: emptyList()
            Napier.d("Plant is $plant")
            send(plant?.toDomainModel(sensors.filterNotNull()))
        }
    }.flowOn(dispatcherIO)

    suspend fun addPlant(
        plantName: String? = null,
        plantDescription: String? = null,
        plantDesiredEnvironment: Map<String, String> = emptyMap(),
        plantSensors: List<String> = emptyList(),
        plantImage: String? = null,
    ) {
        val user = authManager.signedInUser.firstOrNull() ?: return
        Napier.d("Adding plant for user")
        plantRepository.upsertPlantForUser(
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val plantsByUser = authManager.signedInUser.flatMapLatest { user ->
        user?.uid?.let { plantRepository.getPlantsByUser(it) } ?: flowOf(emptyList())
    }.flowOn(dispatcherIO)
}