package nest.planty.data.sqldelight

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nest.planty.db.Click
import nest.planty.db.Database
import nest.planty.db.NetworkClick
import nest.planty.db.Plant
import org.koin.core.annotation.Single

expect suspend fun provideSqlDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver

private suspend fun createDatabase(): Database {
    val driver = provideSqlDriver(Database.Schema)

    val database = Database(
        driver,
        ClickAdapter = Click.Adapter(numberAdapter = IntColumnAdapter),
        NetworkClickAdapter = NetworkClick.Adapter(numberAdapter = IntColumnAdapter),
        PlantAdapter = Plant.Adapter(
            brokersAdapter = listAdapter,
            desiredEnvironmentAdapter = mapAdapter,
            sensorEventsAdapter = sensorEventAdapter,
            sensorsAdapter = listAdapter
        )
    )

    Napier.d("Database created")
    return database
}

@Single
fun provideDatabaseFlow(coroutineScopeIO: CoroutineScope): StateFlow<Database?> {
    val stateFlow = MutableStateFlow<Database?>(null)
    coroutineScopeIO.launch {
        createDatabase().apply { stateFlow.update { this } }
    }
    return stateFlow
}
