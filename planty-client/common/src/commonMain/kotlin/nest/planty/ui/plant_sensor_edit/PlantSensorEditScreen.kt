package nest.planty.ui.plant_sensor_edit

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import nest.planty.Res
import nest.planty.domain.model.DomainSensor
import org.koin.core.parameter.parametersOf

class PlantSensorEditScreen(private val plantUUID: String) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<PlantSensorEditScreenModel> { parametersOf(plantUUID) }
        val availableSensors by screenModel.availableSensors.collectAsState()
        val assignedSensors by screenModel.assignedSensors.collectAsState()
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AssignedSensorList(
                assignedSensors = assignedSensors,
                unassignSensor = screenModel::unassignSensor
            )
            AvailableSensorList(
                availableSensors = availableSensors,
                assignSensor = screenModel::assignSensor
            )
        }
    }

    @Composable
    fun AssignedSensorList(
        assignedSensors: List<DomainSensor>,
        unassignSensor: (uuid: String) -> Unit,
    ) {
        Column {
            Text(text = Res.string.assigned_sensors)
            Crossfade(
                targetState = assignedSensors.isEmpty()
            ) {
                if (it) {
                    Text(text = Res.string.no_assigned_sensors)
                } else {
                    LazyColumn {
                        items(assignedSensors) { sensor ->
                            SensorCard(
                                sensor = sensor,
                                onClick = { unassignSensor(sensor.uuid) }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun AvailableSensorList(
        availableSensors: List<DomainSensor>,
        assignSensor: (uuid: String) -> Unit,
    ) {
        Column {
            Text(text = Res.string.available_sensors)
            Crossfade(
                targetState = availableSensors.isEmpty()
            ) {
                if (it) {
                    Text(text = Res.string.no_available_sensors)
                } else {
                    LazyColumn {
                        items(availableSensors) { sensor ->
                            SensorCard(
                                sensor = sensor,
                                onClick = { assignSensor(sensor.uuid) }
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SensorCard(
        sensor: DomainSensor,
        onClick: () -> Unit,
    ) {
        Card(
            onClick = onClick
        ) {
            Text(text = sensor.type)
        }
    }
}