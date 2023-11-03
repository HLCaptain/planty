package nest.planty.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import nest.planty.Res
import nest.planty.getPlatformName

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        HomeScreen()
    }

    @Composable
    internal fun HomeScreen() {
        val screenModel = getScreenModel<HomeScreenModel>()
        val counter by screenModel.counter.collectAsState()
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
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
                        Text("Counter: $counter")
                    }
                }
            }
        }
    }
}