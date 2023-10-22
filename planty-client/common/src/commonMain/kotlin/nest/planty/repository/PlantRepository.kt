package nest.planty.repository

import nest.planty.data.sqldelight.PlantDataSource
import org.koin.core.annotation.Factory

@Factory
class PlantRepository(
    private val plantDataSource: PlantDataSource,
) {
}