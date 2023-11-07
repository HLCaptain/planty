package nest.planty

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.initialize
import dev.gitlive.firebase.options
import nest.common.BuildConfig
import nest.planty.ui.home.HomeScreen
import nest.planty.ui.theme.PlantyTheme
import org.koin.compose.KoinContext
import org.koin.compose.getKoin
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        PlantyTheme {
            Navigator(HomeScreen())
        }
    }
}
