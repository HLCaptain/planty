package nest.planty.ui.plant_details

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import nest.planty.manager.PlantManager
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class PlantDetailsScreenModel(
    @InjectedParam val plantUUID: String,
    val plantManager: PlantManager,
) : ScreenModel {
    val plant = plantManager.getPlant(plantUUID)
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            null
        )
}