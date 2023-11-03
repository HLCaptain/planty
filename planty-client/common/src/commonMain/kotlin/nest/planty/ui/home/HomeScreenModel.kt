package nest.planty.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nest.planty.manager.ClickManager
import nest.planty.manager.PlantManager
import org.koin.core.annotation.Factory

@Factory
class HomeScreenModel(
    private val plantManager: PlantManager,
    private val clickManager: ClickManager
) : ScreenModel {
    val counter = clickManager.clickCount.stateIn(screenModelScope, SharingStarted.Eagerly, null)

    fun testCall() {
        Napier.d("Hello from HomeScreenModel")
        plantManager.testCall()
    }

    fun incrementCounter() {
        screenModelScope.launch {
            clickManager.incrementClickCount()
        }
    }

    fun resetCounter() {
        screenModelScope.launch {
            clickManager.resetClickCount()
        }
    }
}
