package nest.planty

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.compose.KoinApplication
import org.koin.ksp.generated.defaultModule

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
            KoinApplication(application = { defaultModule() }) {
                UIShow()
            }
        }
    }
}
