plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    application
}

dependencies {
    implementation(project(":common"))
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
}

application {
    mainClass = "nest.planty.MainKt"
    version = project.version.toString()
}
