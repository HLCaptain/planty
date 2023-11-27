package nest.planty.ui.paired_brokers

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.manager.BrokerManager
import nest.planty.manager.SensorManager
import nest.planty.ui.paired_brokers.model.toUiModel
import org.koin.core.annotation.Factory

@Factory
class PairedBrokersScreenModel(
    sensorManager: SensorManager,
    private val brokerManager: BrokerManager,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) : ScreenModel {
    val ownedBrokers = combine(
        brokerManager.ownedBrokers,
        sensorManager.availableSensors
    ) { ownedBrokers, availableSensors ->
        Napier.d("Owned brokers: $ownedBrokers")
        Napier.d("Available sensors: $availableSensors")
        val uiSensors = availableSensors.map { it.toUiModel() }
        val uiBrokers = ownedBrokers?.map { it.toUiModel(uiSensors) } ?: emptyList()
        Napier.d("UI Brokers: $uiBrokers")
        uiBrokers
    }.stateIn(
        screenModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    fun disownBroker(brokerUUID: String) {
        screenModelScope.launch(dispatcherIO) {
            brokerManager.disownBroker(brokerUUID)
        }
    }
}