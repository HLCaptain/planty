plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    application
}

dependencies {
    implementation(project(":common"))
    implementation(compose.desktop.currentOs)
    implementation(libs.koin.compose)
    implementation(libs.kotlinx.coroutines.swing)
}

application {
    mainClass = "nest.planty.MainKt"
}
