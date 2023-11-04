package nest.planty.data.network

import kotlinx.coroutines.flow.Flow

interface ClickNetworkDataSource {
    suspend fun incrementClickCount()
    suspend fun resetClickCount()
    val clickCount: Flow<Int>
}