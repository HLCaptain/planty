package com.example.common

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String {
    return "demo"
}

@Composable
fun UIShow() {
    App()
}