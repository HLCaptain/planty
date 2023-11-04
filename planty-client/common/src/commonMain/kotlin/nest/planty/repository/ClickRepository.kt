package nest.planty.repository

import nest.planty.data.disk.ClickDiskDataSource
import nest.planty.data.network.ClickNetworkDataSource
import org.koin.core.annotation.Factory

@Factory
class ClickRepository(
    private val clickDiskDataSource: ClickDiskDataSource,
    private val clickNetworkDataSource: ClickNetworkDataSource
) {
    suspend fun incrementClickCount() {
        clickDiskDataSource.incrementClickCount()
    }

    suspend fun resetClickCount() {
        clickDiskDataSource.resetClickCount()
    }

    val clickCount = clickDiskDataSource.clickCount
}