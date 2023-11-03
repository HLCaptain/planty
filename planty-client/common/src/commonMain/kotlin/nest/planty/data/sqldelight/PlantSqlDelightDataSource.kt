package nest.planty.data.sqldelight

import io.github.aakira.napier.Napier
import nest.planty.data.disk.PlantDiskDataSource
import nest.planty.db.Database
import org.koin.core.annotation.Factory

@Factory
class PlantSqlDelightDataSource(
    private val database: Database
) : PlantDiskDataSource {
    override fun testCall() {
        Napier.d("Hello from PlantSqlDelightDataSource")
    }
}