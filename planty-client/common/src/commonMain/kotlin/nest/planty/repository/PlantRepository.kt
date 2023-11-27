package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nest.planty.data.store.PlantMutableStoreBuilder
import nest.planty.data.store.PlantsByUserMutableStoreBuilder
import nest.planty.db.Plant
import nest.planty.di.NamedCoroutineScopeIO
import org.koin.core.annotation.Single
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest

@OptIn(ExperimentalStoreApi::class)
@Single
class PlantRepository(
    plantMutableStoreBuilder: PlantMutableStoreBuilder,
    plantsByUserMutableStoreBuilder: PlantsByUserMutableStoreBuilder,
    @NamedCoroutineScopeIO private val coroutineScopeIO: CoroutineScope,
) {
    private val plantMutableStore = plantMutableStoreBuilder.store
    private val plantsMutableStore = plantsByUserMutableStoreBuilder.store

    private val plantStateFlows = mutableMapOf<String, StateFlow<Pair<Plant?, Boolean>>>()
    fun getPlant(uuid: String): StateFlow<Pair<Plant?, Boolean>> {
        return plantStateFlows.getOrPut(uuid) {
            plantMutableStore.stream<StoreReadResponse<Plant>>(
                StoreReadRequest.fresh(key = uuid)
            ).dropWhile {
                it is StoreReadResponse.Loading
            }.map {
                it.throwIfError()
                Napier.d("Read Response: $it")
                val data = it.dataOrNull()
                Napier.d("Plant is $data")
                data to (it is StoreReadResponse.Loading)
            }.stateIn(
                coroutineScopeIO,
                SharingStarted.Eagerly,
                null to true
            )
        }
    }

    suspend fun upsertPlant(plant: Plant) {
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

    private val userPlantStateFlows = mutableMapOf<String, StateFlow<Pair<List<Plant>?, Boolean>>>()
    fun getPlantsByUser(userUUID: String): StateFlow<Pair<List<Plant>?, Boolean>> {
        return userPlantStateFlows.getOrPut(userUUID) {
            plantsMutableStore.stream<StoreReadResponse<List<Plant>>>(
                StoreReadRequest.fresh(key = userUUID)
            ).dropWhile {
                it is StoreReadResponse.Loading
            }.map {
                it.throwIfError()
                Napier.d("Read Response: $it")
                val data = it.dataOrNull()
                Napier.d("Plants are $data")
                data?.sortedBy { plant -> plant.name } to (it is StoreReadResponse.Loading)
            }.stateIn(
                coroutineScopeIO,
                SharingStarted.Eagerly,
                null to true
            )
        }
    }
}
