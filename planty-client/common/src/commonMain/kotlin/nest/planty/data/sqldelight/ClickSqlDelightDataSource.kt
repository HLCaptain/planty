package nest.planty.data.sqldelight

import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nest.planty.data.disk.ClickDiskDataSource
import nest.planty.db.Database
import org.koin.core.annotation.Factory

@Factory
class ClickSqlDelightDataSource(
    private val database: Database
) : ClickDiskDataSource {
    override suspend fun incrementClickCount() {
        database.clickQueries.incrementClicks()
    }

    override suspend fun resetClickCount() {
        database.clickQueries.resetClicks()
    }

    override val clickCount: Flow<Int>
        get() = database.clickQueries.getClicks().asFlow().map { it.executeAsOne().toInt() }
}