package nest.planty.ui.pairing_brokers

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.manager.BrokerManager
import org.koin.core.annotation.Factory

@Factory
class PairingBrokerScreenModel(
    private val brokerManager: BrokerManager,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) : ScreenModel {
    val pairingBrokers = brokerManager.pairingBrokers
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )

    fun pairBroker(uuid: String) {
        screenModelScope.launch(dispatcherIO) {
            brokerManager.ownBroker(uuid)
        }
    }
}