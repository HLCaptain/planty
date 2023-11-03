package nest.planty.data.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import io.github.aakira.napier.Napier
import nest.planty.db.Database
import org.koin.core.annotation.Single

@Single
expect class DriverFactory {
    fun createDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver
}

@Single
fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver(Database.Schema)
    val database = Database(driver)

    Napier.d("Database created")
    return database
}
