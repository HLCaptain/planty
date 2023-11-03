package nest.planty.manager

import io.github.aakira.napier.Napier
import nest.planty.repository.PlantRepository
import org.koin.core.annotation.Factory

@Factory
class PlantManager(
    private val plantRepository: PlantRepository
) {
    fun testCall() {
        Napier.d("Hello from PlantManager")
        plantRepository.testCall()
    }
}