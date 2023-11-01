plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
//    alias(libs.plugins.icerock.resources)
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.runtime)
                implementation(compose.runtimeSaveable)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(libs.napier)
            }
        }
    }
}

compose.experimental {
    web.application {}
}

compose {
//    kotlinCompilerPlugin.set("1.5.3")
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${libs.versions.kotlin}")
}
