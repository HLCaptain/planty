package nest.planty.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nest.planty.data.store.ClickMutableStoreBuilder
import nest.planty.di.NamedClickMutableStore
import nest.planty.di.NamedCoroutineDispatcherIO
import org.koin.core.annotation.Factory
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest

@OptIn(ExperimentalStoreApi::class)
@Factory
class ClickRepository (
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
    @NamedClickMutableStore private val clickMutableStoreBuilder: ClickMutableStoreBuilder
) {
    private val clickMutableStore = clickMutableStoreBuilder.store
    suspend fun incrementClickCount(key: String = "test") {
        val currentClickCount = clickCount.firstOrNull() ?: 0
        Napier.d("Incrementing click count of $currentClickCount")
        clickMutableStore.write(
            StoreWriteRequest.of(
                key = key,
                value = currentClickCount + 1,
            )
        )
    }

    suspend fun resetClickCount(key: String = "test") {
        clickMutableStore.write(
            StoreWriteRequest.of(
                key = key,
                value = 0,
            )
        )
    }

    val clickCount = clickMutableStore.stream<StoreReadResponse<Int>>(
        StoreReadRequest.cached(
            key = "test",
            refresh = true
        )
    ).map {
        val dataOrNull = it.dataOrNull()
        Napier.d("Click count is $dataOrNull")
        dataOrNull
    }.flowOn(dispatcherIO)
}