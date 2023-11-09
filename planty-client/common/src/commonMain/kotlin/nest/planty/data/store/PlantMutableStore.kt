package nest.planty.data.store

import io.github.aakira.napier.Napier
import nest.planty.data.firestore.datasource.PlantFirestoreDataSource
import nest.planty.data.firestore.model.FirestorePlant
import nest.planty.data.mapping.toLocalModel
import nest.planty.data.mapping.toNetworkModel
import nest.planty.data.sqldelight.DatabaseHelper
import nest.planty.db.Plant
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
@NamedPlantMutableStore
fun providePlantMutableStore(
    databaseHelper: DatabaseHelper,
    plantFirestoreDataSource: PlantFirestoreDataSource,
) = MutableStoreBuilder.from(
    fetcher = Fetcher.ofFlow { key ->
        Napier.d("Fetching plant with key $key")
        plantFirestoreDataSource.fetch(uuid = key)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key: String ->
            databaseHelper.queryAsOneFlow {
                Napier.d("User $key has the plant")
                it.plantQueries.select(key)
            }
        },
        writer = { key, local ->
            databaseHelper.withDatabase { db ->
                Napier.d("Writing plant at $key with $local")
                db.plantQueries.insert(local)
            }
        },
        delete = { key ->
            databaseHelper.withDatabase {
                Napier.d("Deleting plant at $key")
                it.plantQueries.delete(key)
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
        post = { _, output ->
            Napier.d("Upserting plant with $output")
            plantFirestoreDataSource.upsert(output.toNetworkModel())
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
    bookkeeper = provideBookkeeper(databaseHelper, Plant::class.simpleName.toString())
)
