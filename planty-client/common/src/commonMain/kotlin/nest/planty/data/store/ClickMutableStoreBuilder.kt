package nest.planty.data.store

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map
import nest.planty.data.sqldelight.DatabaseHelper
import nest.planty.data.store.results.ClickUpdaterResult
import nest.planty.db.Click
import nest.planty.db.NetworkClick
import nest.planty.di.NamedClickMutableStore
import org.koin.core.annotation.Single
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MutableStoreBuilder
import org.mobilenativefoundation.store.store5.OnUpdaterCompletion
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Updater
import org.mobilenativefoundation.store.store5.UpdaterResult

@OptIn(ExperimentalStoreApi::class)
@Single
class ClickMutableStoreBuilder(databaseHelper: DatabaseHelper) {
    val store = provideClickMutableStore(databaseHelper)
}

@OptIn(ExperimentalStoreApi::class)
@Single
@NamedClickMutableStore
fun provideClickMutableStore(
    databaseHelper: DatabaseHelper
) = MutableStoreBuilder.from(
    fetcher = Fetcher.ofFlow { key ->
        databaseHelper.queryAsOneFlow { it.networkClickQueries.select(key) }.map {
            Napier.d("Fetching click count at $key is ${it.number}")
            it.number
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key: String ->
            databaseHelper.queryAsOneFlow { it.clickQueries.select(key) }.map {
                Napier.d("Reading click count at $key is ${it.number}")
                it.number
            }
        },
        writer = { key, local ->
            databaseHelper.withDatabase {
                Napier.d("Writing click count at $key with $local")
                it.clickQueries.upsert(Click(key, local))
            }
        },
        delete = { key ->
            databaseHelper.withDatabase {
                Napier.d("Deleting click count at $key")
                it.clickQueries.delete(key)
            }
        },
        deleteAll = {
            databaseHelper.withDatabase {
                Napier.d("Deleting all click counts")
                it.clickQueries.deleteAll()
            }
        }
    ),
    converter = Converter.Builder<Int, Int, Int>()
        .fromOutputToLocal { it }
        .fromNetworkToLocal { it }
        .build(),
).build(
    updater = Updater.by(
        post = { key, output ->
            databaseHelper.withDatabase {
                Napier.d("Updating $key click count with $output")
                it.networkClickQueries.upsert(NetworkClick(key, output))
            }
            Napier.d("Updated $key click count with $output")
            UpdaterResult.Success.Typed(ClickUpdaterResult.Success(output))
        },
        onCompletion = OnUpdaterCompletion(
            onSuccess = { _ ->
                Napier.d("Successfully updated click count")
            },
            onFailure = { _ ->
                Napier.d("Failed to update click count")
            }
        )
    ),
    bookkeeper = provideBookkeeper(databaseHelper, Click::class.simpleName.toString())
)
