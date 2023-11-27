package nest.planty.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import nest.planty.Res
import nest.planty.db.Plant
import nest.planty.getPlatformName
import nest.planty.ui.components.MenuButton
import nest.planty.ui.dialog.PlantyDialog
import nest.planty.ui.paired_brokers.PairedBrokersScreen
import nest.planty.ui.pairing_brokers.PairingBrokerScreen
import nest.planty.ui.plant_details.PlantDetailsScreen
import nest.planty.ui.profile.ProfileDialogScreen

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        HomeScreen()
    }

    @Composable
    internal fun HomeScreen() {
        val screenModel = getScreenModel<HomeScreenModel>()
        val plants by screenModel.plants.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        Surface {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .scrollable(
                        rememberScrollState(),
                        orientation = Orientation.Vertical
                    )
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = Res.string.hello_x.format(getPlatformName()))

                    PairBrokerButton(onClick = { navigator.push(PairingBrokerScreen()) })

                    PairedBrokersButton(onClick = { navigator.push(PairedBrokersScreen()) })

                    var isProfileDialogShowing by rememberSaveable { mutableStateOf(false) }
                    PlantyDialog(
                        startScreen = ProfileDialogScreen(),
                        isDialogOpen = isProfileDialogShowing,
                        onDialogClosed = { isProfileDialogShowing = false }
                    )

                    Button(onClick = { isProfileDialogShowing = true }) {
                        Text(Res.string.profile)
                    }

                    val isUserSignedIn by screenModel.isUserSignedIn.collectAsState()
                    AnimatedVisibility(
                        visible = isUserSignedIn
                    ) {
                        AddPlantForm(
                            addPlant = { name, description ->
                                screenModel.addPlant(name, description)
                            }
                        )
                    }

                    PlantList(
                        plants = plants,
                        onSelectPlant = { navigator.push(PlantDetailsScreen(it)) },
                        deletePlant = { screenModel.deletePlant(it) },
                    )
                }
            }
        }
    }

    @Composable
    fun AddPlantForm(
        addPlant: (name: String, description: String) -> Unit,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var plantName by rememberSaveable { mutableStateOf("") }
            var plantDescription by rememberSaveable { mutableStateOf("") }
            Text(text = Res.string.add_plant)
            TextField(
                value = plantName,
                onValueChange = { plantName = it },
                placeholder = { Text(Res.string.name) }
            )
            TextField(
                value = plantDescription,
                onValueChange = { plantDescription = it },
                placeholder = { Text(Res.string.description) }
            )
            Button(
                onClick = {
                    addPlant(plantName, plantDescription)
                    plantName = ""
                    plantDescription = ""
                }
            ) {
                Text(Res.string.add_plant)
            }
        }
    }

    @Composable
    fun PlantList(
        plants: List<Plant>,
        onSelectPlant: (uuid: String) -> Unit,
        deletePlant: (uuid: String) -> Unit,
    ) {
        Crossfade(
            modifier = Modifier.animateContentSize(),
            targetState = plants.isEmpty(),
        ) {
            if (it) {
                Text(Res.string.empty_plant_list)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(plants) { plant ->
                        PlantItem(
                            plant = plant,
                            onClick = { onSelectPlant(plant.uuid) },
                            onRemove = { deletePlant(plant.uuid) },
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlantItem(
        plant: Plant,
        onClick: () -> Unit,
        onRemove: () -> Unit,
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp),
            onClick = onClick,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = plant.name ?: Res.string.plant_no_name,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = plant.uuid.take(8),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = plant.description ?: Res.string.plant_no_description,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = onRemove
                ) {
                    Text(Res.string.remove_plant)
                }
            }
        }
    }

    @Composable
    fun PairBrokerButton(
        onClick: () -> Unit,
    ) {
        MenuButton(
            onClick = onClick,
            text = Res.string.pair_broker
        )
    }

    @Composable
    fun PairedBrokersButton(
        onClick: () -> Unit,
    ) {
        MenuButton(
            onClick = onClick,
            text = Res.string.paired_brokers
        )
    }
}