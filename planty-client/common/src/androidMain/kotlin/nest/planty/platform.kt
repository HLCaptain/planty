package nest.planty

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String {
    return "demo"
}

@Composable
fun AppContent() {
    App()
}