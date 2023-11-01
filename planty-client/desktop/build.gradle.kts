plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    application
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.koin.compose)
    implementation(project(":common"))

    // Workaround for https://slack-chats.kotlinlang.org/t/14162593/hello-trying-to-update-kotlin-to-1-9-0-but-with-version-1-4-
    implementation("org.jetbrains.compose.ui:ui-util-desktop:${libs.plugins.jetbrains.compose.get().version}")

    val osName: String = System.getProperty("os.name")
    val targetOs = when {
        osName == "Mac OS X" -> "macos"
        osName.startsWith("Win") -> "windows"
        osName.startsWith("Linux") -> "linux"
        else -> error("Unsupported OS: $osName")
    }
    val targetArch = when (val osArch = System.getProperty("os.arch")) {
        "x86_64", "amd64" -> "x64"
        "aarch64" -> "arm64"
        else -> error("Unsupported arch: $osArch")
    }
    val skikoVersion = "0.7.85" // or any more recent version
    val target = "${targetOs}-${targetArch}"
    implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:$skikoVersion")
}

application {
    mainClass = "nest.planty.MainKt"
}

compose {
//    kotlinCompilerPlugin.set(libs.versions.compose)
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${libs.versions.kotlin}")
}
