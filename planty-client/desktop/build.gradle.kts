import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.icerock.resources)
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common"))
                implementation(libs.koin.compose)
                implementation(libs.icerock.resources.compose)

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
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "nest.planty.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "nest.planty"
            packageVersion = "1.0.0"
        }
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "nest.planty"
}
