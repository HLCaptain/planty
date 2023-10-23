package nest.planty

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String {
    return "demo"
}

actual interface JavaSerializable

@Composable
fun UIShow() {
    App()
}