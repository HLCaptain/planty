package nest.planty.data.disk

import kotlinx.coroutines.flow.Flow

interface ClickDiskDataSource {
    suspend fun incrementClickCount()
    suspend fun resetClickCount()
    val clickCount: Flow<Int>
}