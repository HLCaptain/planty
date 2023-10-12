package nest.planty

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String {
    return "JVM"
}

@Composable
fun UIShow() {
    App()
}