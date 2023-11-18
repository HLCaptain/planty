package nest.planty.ui.plant_sensor_edit

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
            modifier = Modifier
                .padding(8.dp)
                .animateContentSize(),
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
        assignedSensors: Map<String, List<DomainSensor>>,
        unassignSensor: (uuid: String) -> Unit,
    ) {
        Crossfade(
            modifier = Modifier.animateContentSize(),
            targetState = assignedSensors.isEmpty()
        ) {
            if (it) {
                Text(
                    text = Res.string.no_assigned_sensors,
                    style = MaterialTheme.typography.headlineLarge
                )
            } else {
                Column {
                    Text(
                        text = Res.string.assigned_sensors,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Column(
                        modifier = Modifier
                            .animateContentSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        assignedSensors.keys.toList().forEach { key ->
                            Text(
                                text = key.take(16),
                                style = MaterialTheme.typography.titleMedium
                            )
                            LazyColumn(
                                modifier = Modifier
                                    .animateContentSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(assignedSensors[key]!!) { sensor ->
                                    AssignedSensorCard(
                                        sensor = sensor,
                                        unassignSensor = { unassignSensor(sensor.uuid) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun AvailableSensorList(
        availableSensors: Map<String, List<DomainSensor>>,
        assignSensor: (uuid: String) -> Unit,
    ) {
        Crossfade(
            modifier = Modifier.animateContentSize(),
            targetState = availableSensors.isEmpty()
        ) {
            if (it) {
                Text(
                    text = Res.string.no_available_sensors,
                    style = MaterialTheme.typography.headlineLarge
                )
            } else {
                Column {
                    Text(
                        text = Res.string.available_sensors,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Column(
                        modifier = Modifier
                            .animateContentSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableSensors.keys.toList().forEach { key ->
                            Text(
                                text = key.take(16),
                                style = MaterialTheme.typography.titleMedium
                            )
                            LazyColumn(
                                modifier = Modifier
                                    .animateContentSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(availableSensors[key]!!) { sensor ->
                                    AvailableSensorCard(
                                        sensor = sensor,
                                        assignSensor = { assignSensor(sensor.uuid) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun AvailableSensorCard(
        sensor: DomainSensor,
        assignSensor: () -> Unit
    ) {
        Card {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = sensor.type,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = sensor.uuid.take(16),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Button(onClick = assignSensor) {
                    Text(text = Res.string.assign_sensor)
                }
            }
        }
    }

    @Composable
    fun AssignedSensorCard(
        sensor: DomainSensor,
        unassignSensor: () -> Unit
    ) {
        Card {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = sensor.type,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = sensor.uuid.take(16),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Button(onClick = unassignSensor) {
                    Text(text = Res.string.unassign_sensor)
                }
            }
        }
    }
}