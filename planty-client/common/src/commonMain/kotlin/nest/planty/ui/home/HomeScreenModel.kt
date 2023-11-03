package nest.planty.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import nest.planty.manager.PlantManager
import org.koin.core.annotation.Factory

@Factory
class HomeScreenModel(
    private val plantManager: PlantManager
) : ScreenModel {
    private var _counter = MutableStateFlow(0)
    val counter get() = _counter.asStateFlow()

    fun testCall() {
        Napier.d("Hello from HomeScreenModel")
        plantManager.testCall()
    }
    fun incrementCounter() {
        _counter.update { it + 1 }
    }
}
