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
import nest.planty.manager.ClickManager
import org.koin.core.annotation.Factory

@Factory
class HomeScreenModel(
    private val authManager: AuthManager,
    private val clickManager: ClickManager,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) : ScreenModel {
    val counter = clickManager.clickCount.stateIn(screenModelScope, SharingStarted.Eagerly, null)
    val userEmail = authManager.signedInUser.map { it?.email }.stateIn(screenModelScope, SharingStarted.Eagerly, null)
    val userUUID = authManager.signedInUser.map { it?.uid }.stateIn(screenModelScope, SharingStarted.Eagerly, null)
    val isUserSignedIn = authManager.isUserSignedIn.stateIn(screenModelScope, SharingStarted.Eagerly, false)

    fun signInAnonymously() {
        screenModelScope.launch(dispatcherIO) {
            authManager.signInAnonymously()
        }
    }

    fun signOut() {
        screenModelScope.launch(dispatcherIO) {
            authManager.signOut()
        }
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
