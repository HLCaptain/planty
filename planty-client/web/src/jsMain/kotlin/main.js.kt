import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.window.Window
import nest.planty.UIShow
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule

fun main() {
    startKoin {
        defaultModule()
    }
    onWasmReady {
        Window("demo") {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                UIShow()
            }
        }
    }
}