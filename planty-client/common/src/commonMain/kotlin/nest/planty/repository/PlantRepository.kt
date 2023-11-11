package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nest.planty.data.mapping.toLocalModel
import nest.planty.data.store.PlantKey
import nest.planty.data.store.PlantMutableStoreBuilder
import nest.planty.data.store.PlantsByUserKey
import nest.planty.data.store.PlantsByUserMutableStoreBuilder
import nest.planty.db.Plant
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.domain.model.DomainPlant
import org.koin.core.annotation.Factory
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest

@OptIn(ExperimentalStoreApi::class)
@Factory
class PlantRepository(
    plantMutableStoreBuilder: PlantMutableStoreBuilder,
    plantsByUserMutableStoreBuilder: PlantsByUserMutableStoreBuilder,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher
) {
    private val plantMutableStore = plantMutableStoreBuilder.store
    private val plantsMutableStore = plantsByUserMutableStoreBuilder.store
    suspend fun addPlantForUser(plant: Plant) {
        plantMutableStore.write(
            StoreWriteRequest.of(
                key = PlantKey.Write(plant.uuid),
                value = plant,
            )
        )
    }

    suspend fun deletePlantsForUser(userUUID: String) {
        plantsMutableStore.write(
            StoreWriteRequest.of(
                key = PlantsByUserKey.Clear(userUUID),
                value = emptyList(),
            )
        )
    }

    suspend fun deletePlant(uuid: String) {
        plantMutableStore.write(
            StoreWriteRequest.of(
                key = PlantKey.Clear(uuid),
                value = DomainPlant.Default.toLocalModel(),
            )
        )
    }

    fun getPlantsByUser(userUUID: String) =
        plantsMutableStore.stream<StoreReadResponse<List<Plant>>>(
            StoreReadRequest.cached(
                key = PlantsByUserKey.Read(userUUID),
                refresh = true
            )
        ).map {
            it.throwIfError()
            Napier.d("Read Response: $it")
            val data = it.dataOrNull()
            Napier.d("Plants are $data")
            data
        }.flowOn(dispatcherIO)
}
