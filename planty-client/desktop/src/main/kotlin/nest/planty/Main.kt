package nest.planty

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import nest.planty.util.log.initNapier
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule

fun main() = application {
    initNapier()
    startKoin { defaultModule() }
    KoinContext {
        Window(onCloseRequest = ::exitApplication) {
            App()
        }
    }
}
