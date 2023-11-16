package nest.planty.ui.plant

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import nest.planty.Res
import nest.planty.db.Plant
import org.koin.core.parameter.parametersOf

class PlantDetailsScreen(private val plantUUID: String) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<PlantDetailsScreenModel> { parametersOf(plantUUID) }
        val plant by screenModel.plant.collectAsState()
        PlantDetailsScreenContent(plant)
    }

    @Composable
    fun PlantDetailsScreenContent(
        plant: Plant?,
    ) {
        Crossfade(
            targetState = plant
        ) {
            if (it == null) {
                Text(text = "Loading...")
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlantTitle(it.name ?: Res.string.unknown)
                    PlantOwner(it.ownerUUID)
                    PlantDescription(it.description ?: Res.string.unknown)
                }
            }
        }
        // Plant Name (Title)
        // Owned by ownerUUID (Subtitle)
        // Plant Description (Body)
        // Attached sensors (List, item opens to a detail view for a sensor)
        // Desired environment info (List, item opens to an editing view)
        // Monitoring sensorEvents (Item UI, opens to a detail view)

    }

    @Composable
    fun PlantTitle(name: String) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
        )
    }

    @Composable
    fun PlantOwner(ownerUUID: String) {
        Text(
            text = Res.string.plant_owned_by.format(ownerUUID),
            style = MaterialTheme.typography.headlineSmall,
        )
    }

    @Composable
    fun PlantDescription(description: String) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}