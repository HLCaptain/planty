package nest.planty.data.sqldelight

import io.github.aakira.napier.Napier
import nest.planty.data.disk.PlantDiskDataSource
import org.koin.core.annotation.Factory

@Factory
class PlantSqlDelightDataSource(
//    private val databaseHelper: DatabaseHelper
) : PlantDiskDataSource {
    override fun testCall() {
        Napier.d("Hello from PlantSqlDelightDataSource")
    }
}