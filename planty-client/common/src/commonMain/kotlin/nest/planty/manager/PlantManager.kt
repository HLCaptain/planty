package nest.planty.manager

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import nest.planty.db.Plant
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.repository.PlantRepository
import nest.planty.util.log.randomUUID
import org.koin.core.annotation.Factory

@Factory
class PlantManager(
    private val authManager: AuthManager,
    private val plantRepository: PlantRepository,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) {
    suspend fun addPlant(
        plantName: String? = null,
        plantDescription: String? = null,
        plantDesiredEnvironment: Map<String, String> = emptyMap(),
        plantSensors: List<String> = emptyList(),
        plantBrokers: List<String> = emptyList(),
        plantImage: String? = null,
    ) {
        val user = authManager.signedInUser.firstOrNull() ?: return
        Napier.d("Adding plant for user")
        plantRepository.addPlantForUser(
            Plant(
                uuid = randomUUID(),
                ownerUUID = user.uid,
                name = plantName,
                description = plantDescription,
                desiredEnvironment = plantDesiredEnvironment,
                sensorEvents = emptyList(),
                sensors = plantSensors,
                brokers = plantBrokers,
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
        user?.uid?.let { plantRepository.getPlantsByUser(it) } ?: emptyFlow()
    }.flowOn(dispatcherIO)
}