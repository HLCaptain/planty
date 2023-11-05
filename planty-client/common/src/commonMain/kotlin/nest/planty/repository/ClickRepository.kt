package nest.planty.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nest.planty.data.sqldelight.DatabaseHelper
import nest.planty.db.Click
import nest.planty.db.ClickBookkeeper
import nest.planty.db.NetworkClick
import nest.planty.di.NamedCoroutineDispatcherIO
import org.koin.core.annotation.Factory
import org.mobilenativefoundation.store.store5.Bookkeeper
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MutableStoreBuilder
import org.mobilenativefoundation.store.store5.OnUpdaterCompletion
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest
import org.mobilenativefoundation.store.store5.Updater
import org.mobilenativefoundation.store.store5.UpdaterResult

@Factory
class ClickRepository(
//    private val clickDiskDataSource: ClickDiskDataSource,
//    private val clickNetworkDataSource: ClickNetworkDataSource,
    private val databaseHelper: DatabaseHelper,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher
) {
    @OptIn(ExperimentalStoreApi::class, ExperimentalCoroutinesApi::class)
    private val store = MutableStoreBuilder.from(
        fetcher = Fetcher.ofFlow { key ->
            databaseHelper
                .withDatabaseResult { it.networkClickQueries.select(key) }
                .flatMapLatest { query ->
                    query.asFlow().map {
                        val number = it.awaitAsOne().number
                        Napier.d("Fetching click count at $key is $number")
                        number
                    }
                }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key: String ->
                databaseHelper
                    .withDatabaseResult { it.clickQueries.select(key) }
                    .flatMapLatest { query ->
                        query.asFlow().map {
                            val number = it.awaitAsOne().number
                            Napier.d("Reading click count at $key is $number")
                            number
                        }
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
//                    Napier.d("Updating $key click count with $output")
                    it.networkClickQueries.upsert(NetworkClick(key, output))
                }
                Napier.d("Updated $key click count with $output")
                UpdaterResult.Success.Typed(output)
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
        bookkeeper = Bookkeeper.by(
            getLastFailedSync = { key: String ->
                databaseHelper
                    .withDatabaseResult { it.clickBookkeeperQueries.select(key) }
                    .flatMapLatest { query ->
                        Napier.d("Getting last failed sync for $key")
                        query.asFlow().map {
                            val timestamp = it.awaitAsOneOrNull()?.timestamp
                            Napier.d("Last failed sync for $key is $timestamp")
                            timestamp
                        }
                    }.firstOrNull()
            },
            setLastFailedSync = { key, timestamp ->
                databaseHelper.withDatabase {
                    Napier.d("Setting last failed sync for $key to $timestamp")
                    it.clickBookkeeperQueries.upsert(ClickBookkeeper(key, timestamp))
                }
                true
            },
            clear = { key ->
                databaseHelper.withDatabase {
                    Napier.d("Clearing last failed sync for $key")
                    it.clickBookkeeperQueries.delete(key)
                }
                true
            },
            clearAll = {
                databaseHelper.withDatabase {
                    Napier.d("Clearing all last failed syncs")
                    it.clickBookkeeperQueries.deleteAll()
                }
                true
            }
        )
    )

    @OptIn(ExperimentalStoreApi::class)
    suspend fun incrementClickCount(key: String = "test") {
        val currentClickCount = clickCount.firstOrNull() ?: 0
        Napier.d("Incrementing click count of $currentClickCount")
        store.write(
            StoreWriteRequest.of(
                key = key,
                value = currentClickCount + 1,
            )
        )
    }

    @OptIn(ExperimentalStoreApi::class)
    suspend fun resetClickCount(key: String = "test") {
        store.write(
            StoreWriteRequest.of(
                key = key,
                value = 0,
            )
        )
    }

    @OptIn(ExperimentalStoreApi::class)
    val clickCount = store.stream<StoreReadResponse<Int>>(
        StoreReadRequest.cached(
            key = "test",
            refresh = true
        )
    ).map {
        val dataOrNull = it.dataOrNull()
        Napier.d("Click count is $dataOrNull")
        dataOrNull
    }.flowOn(dispatcherIO)
}