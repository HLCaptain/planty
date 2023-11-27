package nest.planty.ui.plant_details

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nest.planty.di.NamedCoroutineDispatcherIO
import nest.planty.manager.PlantManager
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class PlantDetailsScreenModel(
    @InjectedParam val plantUUID: String,
    val plantManager: PlantManager,
    @NamedCoroutineDispatcherIO val dispatcherIO: CoroutineDispatcher
) : ScreenModel {
    val plant = plantManager.getPlant(plantUUID)
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            null
        )

    val desiredFloatingEnvironmentVariables = plant.map { plant ->
        plant?.sensors
            ?.associate { sensor -> sensor.type to 0.0 }
            ?.plus(
                plant.desiredEnvironment
                    .filter { it.value.toDoubleOrNull() != null }
                    .mapValues { it.value.toDouble() }
            ) ?: emptyMap()
    }.stateIn(
        screenModelScope,
        SharingStarted.Eagerly,
        emptyMap()
    )

    fun setDesiredFloatingEnvironmentVariable(name: String, value: Double) {
        screenModelScope.launch(dispatcherIO) {
            plantManager.setDesiredEnvironmentVariable(plantUUID, name, value)
        }
    }

    fun setDesiredFloatingEnvironmentVariableMap(floatDesiredEnvironmentVariableMap: Map<String, Double>) {
        screenModelScope.launch(dispatcherIO) {
            plantManager.setDesiredEnvironmentVariableMap(plantUUID, floatDesiredEnvironmentVariableMap)
        }
    }
}
