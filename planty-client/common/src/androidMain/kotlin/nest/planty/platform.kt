package nest.planty

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String {
    return "Android"
}

actual typealias JavaSerializable = java.io.Serializable

@Composable
fun AppContent() {
    App()
}