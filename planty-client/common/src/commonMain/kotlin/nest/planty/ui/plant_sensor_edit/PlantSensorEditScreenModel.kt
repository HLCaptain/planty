package nest.planty.ui.plant_sensor_edit

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
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
    val availableSensors = sensorManager.availableSensors
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )

    val assignedSensors = sensorManager.getSensorsForPlant(plantUUID)
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            emptyList()
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