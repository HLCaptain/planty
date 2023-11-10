package nest.planty.data.store

import dev.gitlive.firebase.firebaseSerializer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map
import nest.planty.data.firestore.datasource.PlantFirestoreDataSource
import nest.planty.data.firestore.model.FirestorePlant
import nest.planty.data.mapping.toLocalModel
import nest.planty.data.mapping.toNetworkModel
import nest.planty.data.sqldelight.DatabaseHelper
import nest.planty.db.Plant
import nest.planty.di.NamedPlantsMutableStore
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
 * Provides a [MutableStore] for [Plant]s by authenticated user.
 * @param databaseHelper The [DatabaseHelper] to use for local storage.
 * @param plantFirestoreDataSource The [PlantFirestoreDataSource] to use for remote storage.
 * @return A [MutableStore] for [Plant]s.
 * @see provideBookkeeper
 */
@OptIn(ExperimentalStoreApi::class)
@Single
class PlantsMutableStoreBuilder(
    databaseHelper: DatabaseHelper,
    plantFirestoreDataSource: PlantFirestoreDataSource,
) {
    val store = providePlantsMutableStore(databaseHelper, plantFirestoreDataSource)
}

@OptIn(ExperimentalStoreApi::class)
@NamedPlantsMutableStore
fun providePlantsMutableStore(
    databaseHelper: DatabaseHelper,
    plantFirestoreDataSource: PlantFirestoreDataSource,
) = MutableStoreBuilder.from(
    fetcher = Fetcher.ofFlow { key ->
        plantFirestoreDataSource.fetchByUser(userUUID = key)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key: String ->
            databaseHelper.queryAsListFlow { it.plantQueries.getPlantsForUser(key) }.map {
                Napier.d("User $key has ${it.size} plants")
                it
            }
        },
        writer = { key, local ->
            databaseHelper.withDatabase { db ->
                local.forEach {
                    Napier.d("Writing plant at $key with $it")
                    db.plantQueries.insert(it)
                }
            }
        },
        delete = { key ->
            databaseHelper.withDatabase {
                Napier.d("Deleting plant at $key")
                it.plantQueries.deleteAllPlantsForUser(key)
            }
        },
        deleteAll = {
            databaseHelper.withDatabase {
                Napier.d("Deleting all plants")
                it.plantQueries.deleteAll()
            }
        }
    ),
    converter = Converter.Builder<List<FirestorePlant>, List<Plant>, List<Plant>>()
        .fromOutputToLocal { it }
        .fromNetworkToLocal { network -> network.map { it.toLocalModel() } }
        .build(),
).build(
    updater = Updater.by(
        post = { _, output ->
            output.map { plantFirestoreDataSource.upsert(it.toNetworkModel()) }
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
    bookkeeper = provideBookkeeper(databaseHelper, Plant::class.simpleName.toString() + "List")
)
