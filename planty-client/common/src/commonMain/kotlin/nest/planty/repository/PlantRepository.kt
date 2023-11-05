package nest.planty.repository

import org.koin.core.annotation.Factory

@Factory
class PlantRepository(
) {
    fun testCall() {
        println("Hello from PlantRepository")
    }
}