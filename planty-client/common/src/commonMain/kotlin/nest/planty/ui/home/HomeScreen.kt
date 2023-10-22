package nest.planty.ui.home

import androidx.compose.foundation.Image
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
import dev.icerock.moko.resources.compose.painterResource
import nest.planty.MR
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
                    Image(
                        painter = painterResource(MR.images.flower_image),
                        contentDescription = "flower image"
                    )
                    Text(getPlatformName())
                    Button(onClick = { screenModel.incrementCounter() }) {
                        Text("Counter: $counter")
                    }
                }
            }
        }
    }
}