package nest.planty

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import nest.planty.ui.home.HomeScreen
import nest.planty.ui.theme.PlantyTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        PlantyTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Navigator(HomeScreen()) { navigator ->
                    SlideTransition(navigator) {
                        it.Content()
                    }
                }
            }
        }
    }
}
