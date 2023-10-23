import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.icerock.resources)
    alias(libs.plugins.google.ksp)
}

group = "nest"
version = "1.0-SNAPSHOT"

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
    jvmToolchain(17)
    applyDefaultHierarchyTemplate()
    androidTarget()
    jvm("desktop")
    js(IR) { browser() }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                api(compose.runtime)
                api(compose.ui)
                api(compose.foundation)
                api(compose.materialIconsExtended)
                api(compose.material3)
                api(libs.voyager.navigator)
                api(libs.voyager.bottomSheetNavigator)
                api(libs.voyager.tabNavigator)
                api(libs.voyager.transitions)
                api(libs.voyager.koin)
                implementation(libs.ktor.core)
                api(project.dependencies.platform(libs.koin.bom))
                api(libs.koin.core)
                api(libs.koin.annotations)
                implementation(libs.napier)
                implementation(libs.store)
                api(libs.icerock.resources)
                api(libs.icerock.resources.compose)
                implementation(libs.kotlinx.coroutines)
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
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.core)
                implementation(libs.ktor.jvm)
                implementation(libs.koin.android)
                implementation(libs.koin.logger.slf4j)
            }
        }

        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(compose.preview)
                implementation(compose.desktop.common)
                implementation(libs.ktor.jvm)
                implementation(libs.koin.ktor)
                implementation(libs.koin.logger.slf4j)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(compose.html.core)
            }
        }

        val desktopTest by getting
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp)
//    commonMainApi(libs.icerock.resources)
}

// WORKAROUND: ADD this dependsOn("kspCommonMainKotlinMetadata") instead of above dependencies
tasks.withType<KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
afterEvaluate {
    tasks.filter {
        it.name.contains("SourcesJar", true)
    }.forEach {
        println("SourceJarTask====>${it.name}")
        it.dependsOn("kspCommonMainKotlinMetadata")
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
//    arg("KOIN_DEFAULT_MODULE", "false")
}

android {
    namespace = "nest.planty"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
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
