package nest.planty.repository

import io.github.aakira.napier.Napier
import nest.planty.data.disk.PlantDiskDataSource
import org.koin.core.annotation.Factory

@Factory
class PlantRepository(
    private val plantDiskDataSource: PlantDiskDataSource,
) {
    fun testCall() {
        Napier.d("Hello from PlantRepository")
        plantDiskDataSource.testCall()
    }
}