package nest.planty.repository

import nest.planty.data.network.PlantNetworkDataSource
import org.koin.core.annotation.Factory

@Factory
class PlantRepository(
    private val plantNetworkDataSource: PlantNetworkDataSource
) {
    fun testCall() {
        println("Hello from PlantRepository")
    }
}
