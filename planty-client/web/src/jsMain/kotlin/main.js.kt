import androidx.compose.ui.window.Window
import nest.planty.App
import nest.planty.util.log.initNapier
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule

fun main() {
    initNapier()
    startKoin { defaultModule() }
    onWasmReady {
        Window("demo") {
            App()
        }
    }
}