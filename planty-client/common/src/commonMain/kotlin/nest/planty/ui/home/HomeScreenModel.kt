package nest.planty.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nest.planty.di.NamedCoroutineDispatcherDefault
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.di.NamedCoroutineDispatcherMain
import nest.planty.manager.ClickManager
import org.koin.core.annotation.Factory

@Factory
class HomeScreenModel(
//    private val plantManager: PlantManager,
    private val clickManager: ClickManager,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) : ScreenModel {
    val counter = clickManager.clickCount.stateIn(screenModelScope, SharingStarted.Eagerly, null)

    fun testCall() {
//        Napier.d("Hello from HomeScreenModel")
//        plantManager.testCall()
    }

    fun incrementCounter() {
        // DispatcherIO (or at least not the default Dispatcher) must be used on Desktop due to dependency issues
        // https://github.com/adrielcafe/voyager/issues/147#issuecomment-1717612820
        screenModelScope.launch(dispatcherIO) {
            clickManager.incrementClickCount()
        }
    }

    fun resetCounter() {
        screenModelScope.launch(dispatcherIO) {
            clickManager.resetClickCount()
        }
    }
}
