package nest.planty.data.store

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import nest.planty.data.sqldelight.DatabaseHelper
import nest.planty.db.DataHistory
import org.mobilenativefoundation.store.store5.Bookkeeper

fun provideBookkeeper(
    databaseHelper: DatabaseHelper,
    originTable: String
) = Bookkeeper.by(
    getLastFailedSync = { key: String ->
        databaseHelper.queryAsOneOrNullFlow { it.dataHistoryQueries.select(key) }.map {
            Napier.d("Get last failed sync for $key is ${it?.timestamp}")
            it?.timestamp
        }.firstOrNull()
    },
    setLastFailedSync = { key, timestamp ->
        databaseHelper.withDatabase {
            Napier.d("Setting last failed sync for $key to $timestamp")
            it.dataHistoryQueries.upsert(DataHistory(key, timestamp, originTable))
        }
        true
    },
    clear = { key ->
        databaseHelper.withDatabase {
            Napier.d("Clearing last failed sync for $key")
            it.dataHistoryQueries.delete(key)
        }
        true
    },
    clearAll = {
        databaseHelper.withDatabase {
            Napier.d("Clearing all last failed syncs")
            it.dataHistoryQueries.deleteAll()
        }
        true
    }
)