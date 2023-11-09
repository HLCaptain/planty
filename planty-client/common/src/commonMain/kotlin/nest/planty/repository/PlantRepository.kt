package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nest.planty.data.store.PlantMutableStoreBuilder
import nest.planty.data.store.PlantsMutableStoreBuilder
import nest.planty.db.Plant
import nest.planty.di.NamedCoroutineDispatcherIO
import org.koin.core.annotation.Factory
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest

@OptIn(ExperimentalStoreApi::class)
@Factory
class PlantRepository(
    plantMutableStoreBuilder: PlantMutableStoreBuilder,
    plantsMutableStoreBuilder: PlantsMutableStoreBuilder,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher
) {
    private val plantMutableStore = plantMutableStoreBuilder.store
    private val plantsMutableStore = plantsMutableStoreBuilder.store
    suspend fun addPlantForUser(plant: Plant) {
        plantMutableStore.write(
            StoreWriteRequest.of(
                key = plant.uuid,
                value = plant,
            )
        )
    }

    suspend fun deletePlantsForUser(userUUID: String) {
        plantsMutableStore.clear(userUUID)
    }

    suspend fun deletePlant(uuid: String) {
        plantMutableStore.clear(uuid)
    }

    fun getPlantsByUser(userUUID: String) =
        plantsMutableStore.stream<StoreReadResponse<List<Plant>>>(
            StoreReadRequest.cached(
                key = userUUID,
                refresh = true
            )
        ).map {
            val dataOrNull = it.dataOrNull()
            Napier.d("Plants are $dataOrNull")
            dataOrNull
        }.flowOn(dispatcherIO)
}
