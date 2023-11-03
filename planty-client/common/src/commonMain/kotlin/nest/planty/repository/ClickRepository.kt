package nest.planty.repository

import nest.planty.data.disk.ClickDiskDataSource
import org.koin.core.annotation.Factory

@Factory
class ClickRepository(
    private val clickDiskDataSource: ClickDiskDataSource
) {
    suspend fun incrementClickCount() {
        clickDiskDataSource.incrementClickCount()
    }

    suspend fun resetClickCount() {
        clickDiskDataSource.resetClickCount()
    }

    val clickCount = clickDiskDataSource.clickCount
}