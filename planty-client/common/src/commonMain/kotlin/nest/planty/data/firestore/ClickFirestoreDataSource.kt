package nest.planty.data.firestore

import kotlinx.coroutines.flow.Flow
import nest.planty.data.disk.ClickDiskDataSource
import nest.planty.data.network.ClickNetworkDataSource
import org.koin.core.annotation.Factory

// FIXME: mimicing the ClickSqlDelightDataSource for now
@Factory
class ClickFirestoreDataSource(
    private val clickDiskDataSource: ClickDiskDataSource,
) : ClickNetworkDataSource {
    override suspend fun incrementClickCount() {
        clickDiskDataSource.incrementClickCount()
    }

    override suspend fun resetClickCount() {
        clickDiskDataSource.resetClickCount()
    }

    override val clickCount: Flow<Int>
        get() = clickDiskDataSource.clickCount
}