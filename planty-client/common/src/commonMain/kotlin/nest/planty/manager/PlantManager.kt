package nest.planty.manager

import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import nest.planty.db.Plant
import nest.planty.domain.model.DomainUser
import nest.planty.repository.PlantRepository
import nest.planty.util.log.randomUUID
import org.koin.core.annotation.Factory

@Factory
class PlantManager(
    private val authManager: AuthManager,
    private val plantRepository: PlantRepository
) {
    suspend fun addPlant(
        plantName: String? = null,
        plantDescription: String? = null,
        plantDesiredEnvironment: Map<String, String> = emptyMap(),
        plantSensors: List<String> = emptyList(),
        plantBrokers: List<String> = emptyList(),
        plantImage: String? = null,
    ) {
        Napier.d("Adding plant for user")
        val user = authManager.signedInUser.firstOrNull() ?: DomainUser.LocalUser
        plantRepository.addPlantForUser(
            Plant(
                uuid = randomUUID(),
                ownerUUID = user.uuid,
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
        val user = authManager.signedInUser.firstOrNull() ?: DomainUser.LocalUser
        plantRepository.deletePlantsForUser(user.uuid)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val plantsByUser = authManager.signedInUser.flatMapLatest { user ->
        plantRepository.getPlantsByUser(user?.uuid ?: DomainUser.LocalUser.uuid)
    }
}