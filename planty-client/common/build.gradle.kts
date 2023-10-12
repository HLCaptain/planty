plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.icerock.resources)
}

group = "nest"
version = "1.0-SNAPSHOT"

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
    jvmToolchain(17)
    androidTarget()
    jvm("desktop")
    js(IR) {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.ui)
                api(compose.foundation)
                api(compose.materialIconsExtended)
                api(compose.material3)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)
                implementation(libs.ktor.core)
                implementation(libs.koin.core)
                implementation(libs.napier)
                implementation(libs.store)
                api(libs.icerock.resources)
                api(libs.icerock.resources.compose)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(libs.androidx.appcompat)
                api(libs.androidx.core)
                implementation(libs.ktor.jvm)
                implementation(libs.voyager.androidx)
            }
        }

        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(compose.preview)
                implementation(libs.ktor.jvm)
            }
        }

        val desktopTest by getting

        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(compose.html.core)
                implementation(libs.ktor.js)
                implementation(libs.ktor.jsonjs)
            }
        }

        val jsTest by getting
    }
}

android {
    namespace = "nest.planty"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("nest.planty.db")
        }
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "nest.planty"
}
