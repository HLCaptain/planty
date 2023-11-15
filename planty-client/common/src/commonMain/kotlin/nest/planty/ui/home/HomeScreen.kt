package nest.planty.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import nest.planty.Res
import nest.planty.db.Plant
import nest.planty.getPlatformName
import nest.planty.ui.dialog.PlantyDialog
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
        val counter by screenModel.counter.collectAsState()
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Libres implementation:
//                    Image(
//                        painter = Res.image.flower_image.painterResource(),
//                        contentDescription = "flower image"
//                    )
                    // OR if using compose resources:
//                    Image(
//                        painter = painterResource("flower_image.jpg),
//                        contentDescription = "flower image"
//                    )
                    Text(text = Res.string.hello_x.format(getPlatformName()))

                    Button(onClick = { screenModel.incrementCounter() }) {
                        Crossfade(
                            modifier = Modifier.animateContentSize(),
                            targetState = counter != null
                        ) {
                            if (it && counter != null) {
                                Text(
                                    if (counter == 0) {
                                        Res.string.click_me
                                    } else {
                                        Res.string.clicked_x_times.format(counter!!, counter.toString())
                                    }
                                )
                            } else {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                    Button(onClick = { screenModel.resetCounter() }) {
                        Text(Res.string.reset_clicks)
                    }

                    AnimatedVisibility(
                        modifier = Modifier.animateContentSize(),
                        visible = plants.isEmpty()
                    ) {
                        Text(Res.string.empty_plant_list)
                    }

                    LazyColumn {
                        items(plants) { plant ->
                            PlantItem(
                                plant = plant,
                                onRemove = { screenModel.deletePlant(plant.uuid) }
                            )
                        }
                    }

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
                }
            }
        }
    }

    @Composable
    fun AddPlantForm(
        addPlant: (name: String, description: String) -> Unit,
    ) {
        Column {
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
    fun PlantItem(
        plant: Plant,
        onRemove: () -> Unit,
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row {
                Column {
                    Text(text = plant.uuid)
                    Text(text = plant.name ?: Res.string.plant_no_name)
                    Text(text = plant.description ?: Res.string.plant_no_description)
                }
                Button(onClick = onRemove) {
                    Text(Res.string.remove_plant)
                }
            }
        }
    }
}