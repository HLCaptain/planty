package nest.planty.ui.plant_details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import nest.planty.Res
import nest.planty.domain.model.DomainPlant
import nest.planty.domain.model.DomainSensor
import nest.planty.ui.components.MenuButton
import nest.planty.ui.plant_sensor_edit.PlantSensorEditScreen
import org.koin.core.parameter.parametersOf

class PlantDetailsScreen(private val plantUUID: String) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<PlantDetailsScreenModel> { parametersOf(plantUUID) }
        val plant by screenModel.plant.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        PlantDetailsScreenContent(
            modifier = Modifier.padding(8.dp),
            plant = plant,
            editSensors = { navigator.push(PlantSensorEditScreen(plantUUID)) }
        )
    }

    @Composable
    fun PlantDetailsScreenContent(
        modifier: Modifier = Modifier,
        plant: DomainPlant?,
        editSensors: () -> Unit = {},
    ) {
        Crossfade(
            modifier = modifier,
            targetState = plant
        ) {
            if (it == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp)
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Plant Name (Title)
                // Owned by ownerUUID (Subtitle)
                // Plant Description (Body)
                // Attached sensors (List, item opens to a detail view for a sensor)
                // Desired environment info (List, item opens to an editing view)
                // Monitoring sensorEvents (Item UI, opens to a detail view)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlantTitle(it.name ?: Res.string.unknown)
                    PlantOwner(it.ownerUUID)
                    PlantDescription(it.description ?: Res.string.unknown)
                    AttachedSensors(
                        sensorsTypes = it.sensors,
                        editSensors = editSensors
                    )
                }
            }
        }
    }

    @Composable
    fun PlantTitle(name: String) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineLarge,
        )
    }

    @Composable
    fun PlantOwner(ownerUUID: String) {
        Text(
            text = Res.string.plant_owned_by.format(ownerUUID.take(8)),
            style = MaterialTheme.typography.labelLarge,
        )
    }

    @Composable
    fun PlantDescription(description: String) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
        )
    }

    @Composable
    fun AttachedSensors(
        sensorsTypes: List<DomainSensor>,
        editSensors: () -> Unit = {},
    ) {
        MenuButton(
            text = Res.string.edit_sensors,
            onClick = editSensors
        )
        LazyColumn(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(sensorsTypes) { sensor ->
                SensorItem(sensor)
            }
        }
    }

    @Composable
    fun SensorItem(sensor: DomainSensor) {
        Card {
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sensor.uuid.take(16),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = sensor.type,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}