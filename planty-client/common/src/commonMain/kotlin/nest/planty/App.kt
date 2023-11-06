package nest.planty

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import nest.planty.ui.home.HomeScreen
import nest.planty.ui.theme.PlantyTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        PlantyTheme {
            Navigator(HomeScreen())
        }
    }
}