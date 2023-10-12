package nest.planty

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import nest.planty.ui.home.HomeScreen

@Composable
internal fun App() {
    Navigator(HomeScreen())
}