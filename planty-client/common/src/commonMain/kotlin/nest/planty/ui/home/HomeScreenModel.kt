package nest.planty.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.manager.AuthManager
import nest.planty.manager.PlantManager
import org.koin.core.annotation.Factory

@Factory
class HomeScreenModel(
    private val authManager: AuthManager,
    private val plantManager: PlantManager,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) : ScreenModel {
    val plants = plantManager.plantsByUser
        .map { it ?: emptyList() }
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
    val isUserSignedIn = authManager.isUserSignedIn
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            false
        )

    fun addPlant(
        name: String,
        description: String,
    ) {
        screenModelScope.launch(dispatcherIO) {
            plantManager.addPlant(name, description)
        }
    }

    fun deletePlant(uuid: String) {
        screenModelScope.launch(dispatcherIO) {
            plantManager.deletePlant(uuid)
        }
    }
}
