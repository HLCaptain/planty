package nest.planty.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
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
    fun incrementCounter() {
        _counter.update { it + 1 }
    }
}
