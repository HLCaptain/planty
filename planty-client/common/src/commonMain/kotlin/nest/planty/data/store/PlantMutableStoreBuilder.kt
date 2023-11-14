package nest.planty.data.store

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import nest.planty.data.firestore.datasource.PlantFirestoreDataSource
import nest.planty.data.firestore.model.FirestorePlant
import nest.planty.data.mapping.toLocalModel
import nest.planty.data.mapping.toNetworkModel
import nest.planty.data.sqldelight.DatabaseHelper
import nest.planty.db.Plant
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.di.NamedPlantMutableStore
import org.koin.core.annotation.Single
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MutableStoreBuilder
import org.mobilenativefoundation.store.store5.OnUpdaterCompletion
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Updater
import org.mobilenativefoundation.store.store5.UpdaterResult

/**
 * Provides a [MutableStore] for [Plant]s.
 * @param databaseHelper The [DatabaseHelper] to use for local storage.
 * @param plantFirestoreDataSource The [PlantFirestoreDataSource] to use for remote storage.
 * @return A [MutableStore] for [Plant]s.
 * @see provideBookkeeper
 */
@OptIn(ExperimentalStoreApi::class)
@Single
class PlantMutableStoreBuilder(
    databaseHelper: DatabaseHelper,
    plantFirestoreDataSource: PlantFirestoreDataSource,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) {
    val store = providePlantMutableStore(databaseHelper, plantFirestoreDataSource, dispatcherIO)
}

@OptIn(ExperimentalStoreApi::class)
@Single
@NamedPlantMutableStore
fun providePlantMutableStore(
    databaseHelper: DatabaseHelper,
    plantFirestoreDataSource: PlantFirestoreDataSource,
    dispatcher: CoroutineDispatcher,
) = MutableStoreBuilder.from(
    fetcher = Fetcher.ofFlow { key ->
        require(key is PlantKey.Read)
        Napier.d("Fetching plant with key $key")
        plantFirestoreDataSource.fetch(uuid = key.uuid)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key: PlantKey ->
            require(key is PlantKey.Read)
            databaseHelper.queryAsOneFlow {
                Napier.d("User $key has the plant")
                it.plantQueries.select(key.uuid)
            }
        },
        writer = { key, local ->
            when (key) {
                is PlantKey.Write -> {
                    databaseHelper.withDatabase { db ->
                        Napier.d("Writing plant at $key with $local")
                        db.plantQueries.upsert(local)
                    }
                }
                is PlantKey.Clear -> {
                    databaseHelper.withDatabase { db ->
                        Napier.d("Clear plant at $key")
                        db.plantQueries.delete(key.uuid)
                    }
                    plantFirestoreDataSource.delete(key.uuid)
                }
                else -> Napier.e("Not writing key $key")
            }
        },
        delete = { key ->
            require(key is PlantKey.Clear)
            databaseHelper.withDatabase {
                Napier.d("Deleting plant at $key")
                it.plantQueries.delete(key.uuid)
            }
        },
        deleteAll = {
            databaseHelper.withDatabase {
                Napier.d("Deleting all plants")
                it.plantQueries.deleteAll()
            }
        }
    ),
    converter = Converter.Builder<FirestorePlant, Plant, Plant>()
        .fromOutputToLocal { it }
        .fromNetworkToLocal { it.toLocalModel() }
        .build(),
).build(
    updater = Updater.by(
        post = { key, output ->
            when (key) {
                is PlantKey.Write -> {
                    Napier.d("Upserting plant with $output")
                    plantFirestoreDataSource.upsert(output.toNetworkModel())
                }
                is PlantKey.Clear -> {
                    Napier.d("Clearing plant with $output")
                    plantFirestoreDataSource.delete(output.toNetworkModel()).flowOn(dispatcher).first()
                }
                else -> Napier.e("Not updating key $key")
            }
            UpdaterResult.Success.Typed(output)
        },
        onCompletion = OnUpdaterCompletion(
            onSuccess = { _ ->
                Napier.d("Successfully updated plants")
            },
            onFailure = { _ ->
                Napier.d("Failed to update plants")
            }
        )
    ),
    bookkeeper = provideBookkeeper(
        databaseHelper,
        Plant::class.simpleName.toString()
    ) {
        when (it) {
            is PlantKey.Read -> it.uuid
            is PlantKey.Clear -> it.uuid
            is PlantKey.Write -> it.uuid
        }
    }
)
