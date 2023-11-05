package nest.planty.data.sqldelight

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import nest.planty.data.disk.ClickDiskDataSource
import org.koin.core.annotation.Factory

@Factory
class ClickSqlDelightDataSource(
    private val databaseHelper: DatabaseHelper,
) : ClickDiskDataSource {
    override suspend fun incrementClickCount() {
        databaseHelper.withDatabase { database ->
            Napier.d("Incrementing click count")
            database.clickQueries.incrementClicks()
        }
    }

    override suspend fun resetClickCount() {
        databaseHelper.withDatabase { database ->
            Napier.d("Resetting click count")
            database.clickQueries.resetClicks()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val clickCount: Flow<Int>
        get() = databaseHelper.withDatabaseResult {
            database -> database.clickQueries.getClicks()
        }.flatMapLatest { query ->
            query.asFlow().map {
                val clickCount = it.awaitAsOne().number
                Napier.d("Click count is $clickCount")
                clickCount
            }
        }
}