package nest.planty.ui.plant_sensor_edit

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.manager.SensorManager
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class PlantSensorEditScreenModel(
    @InjectedParam val plantUUID: String,
    private val sensorManager: SensorManager,
    @NamedCoroutineDispatcherIO private val dispatcherIO: CoroutineDispatcher,
) : ScreenModel {
    val assignedSensors = sensorManager.getSensorsForPlant(plantUUID)
        .map { sensors -> sensors.groupBy { it.ownerBroker } }
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            emptyMap()
        )

    val availableSensors = combine(
        sensorManager.availableSensors,
        assignedSensors
    ) { availableSensors, assignedSensors ->
        availableSensors.minus(assignedSensors.values.flatten().distinct().toSet()).groupBy { it.ownerBroker }
    }.stateIn(
        screenModelScope,
        SharingStarted.Eagerly,
        emptyMap()
    )

    fun assignSensor(sensorUUID: String) {
        screenModelScope.launch(dispatcherIO) {
            sensorManager.assignSensorToPlant(sensorUUID, plantUUID)
        }
    }

    fun unassignSensor(sensorUUID: String) {
        screenModelScope.launch(dispatcherIO) {
            sensorManager.unassignSensorFromPlant(sensorUUID, plantUUID)
        }
    }
}