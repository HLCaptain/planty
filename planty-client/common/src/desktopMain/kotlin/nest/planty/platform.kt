package nest.planty

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String {
    return "JVM"
}

actual interface JavaSerializable

@Composable
fun UIShow() {
    App()
}