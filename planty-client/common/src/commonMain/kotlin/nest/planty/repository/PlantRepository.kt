package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nest.planty.db.Plant
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.di.NamedPlantMutableStore
import nest.planty.di.NamedPlantsMutableStore
import org.koin.core.annotation.Factory
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest

@OptIn(ExperimentalStoreApi::class)
@Factory
class PlantRepository(
    @NamedPlantMutableStore private val plantMutableStore: MutableStore<String, Plant>,
    @NamedPlantsMutableStore private val plantsMutableStore: MutableStore<String, List<Plant>>,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher
) {
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
