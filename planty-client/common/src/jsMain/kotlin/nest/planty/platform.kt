package nest.planty

import androidx.compose.runtime.Composable
import nest.planty.App

actual fun getPlatformName(): String {
    return "demo"
}

@Composable
fun UIShow() {
    App()
}