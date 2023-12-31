package nest.planty.data.store

import io.github.aakira.napier.Napier
import nest.planty.data.firestore.model.FirestorePlant
import nest.planty.data.mapping.toLocalModel
import nest.planty.data.mapping.toNetworkModel
import nest.planty.data.network.PlantNetworkDataSource
import nest.planty.data.sqldelight.DatabaseHelper
import nest.planty.db.Plant
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
 * @param plantNetworkDataSource The [PlantNetworkDataSource] to use for remote storage.
 * @return A [MutableStore] for [Plant]s.
 * @see provideBookkeeper
 */
@OptIn(ExperimentalStoreApi::class)
@Single
class PlantMutableStoreBuilder(
    databaseHelper: DatabaseHelper,
    plantNetworkDataSource: PlantNetworkDataSource,
) {
    val store = providePlantMutableStore(databaseHelper, plantNetworkDataSource)
}

@OptIn(ExperimentalStoreApi::class)
fun providePlantMutableStore(
    databaseHelper: DatabaseHelper,
    plantNetworkDataSource: PlantNetworkDataSource,
) = MutableStoreBuilder.from(
    fetcher = Fetcher.ofFlow { key ->
        Napier.d("Fetching plant with key $key")
        plantNetworkDataSource.fetch(uuid = key)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key: String ->
            databaseHelper.queryAsOneOrNullFlow {
                Napier.d("Reading plant at $key")
                it.plantQueries.select(key)
            }
        },
        writer = { key, local ->
            databaseHelper.withDatabase { db ->
                Napier.d("Writing plant at $key with $local")
                db.plantQueries.upsert(local)
            }
        },
        delete = { key ->
            databaseHelper.withDatabase {
                Napier.d("Deleting plant at $key")
                it.plantQueries.delete(key)
            }
            plantNetworkDataSource.delete(key)
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
            plantNetworkDataSource.upsert(output.toNetworkModel())
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
    ) { it }
)
