import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.libres)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.services)
}

group = "nest"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(17)
    androidTarget()
    jvm()
    js(IR) { browser() }

    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.materialIconsExtended)
                implementation(compose.material3)
                // FIXME: use compose resources for loading images in the future
//                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
//                implementation(compose.components.resources)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)
                implementation(libs.ktor.core)
                api(project.dependencies.platform(libs.koin.bom))
                api(libs.koin.core)
                implementation(libs.koin.annotations)
                implementation(libs.koin.compose)
                implementation(libs.napier)
                implementation(libs.store)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.libres.compose)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.gitlive.firebase.common)
                implementation(libs.gitlive.firebase.auth)
                implementation(libs.gitlive.firebase.firestore)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.core)
                implementation(libs.ktor.jvm)
                implementation(libs.koin.android)
                implementation(libs.koin.logger.slf4j)
                implementation(libs.sqldelight.android)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.preview)
                implementation(compose.desktop.common)
                implementation(libs.ktor.jvm)
                implementation(libs.koin.ktor)
                implementation(libs.koin.logger.slf4j)
                implementation(libs.sqldelight.jvm)
            }
        }

        jsMain {
            dependencies {
                implementation(compose.html.core)
                implementation(libs.kotlinx.coroutines.js)
                implementation(libs.sqldelight.js)
                implementation(npm("sql.js", "1.8.0"))
                implementation(devNpm("copy-webpack-plugin", "11.0.0"))
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp)
    implementation(project.dependencies.platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.common)
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.firestore)
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
            packageName = "nest.planty.db"
            generateAsync = true
        }
    }
}

libres {
    generatedClassName = "Res" // "Res" by default
    generateNamedArguments = true // false by default
    baseLocaleLanguageCode = "en" // "en" by default
    camelCaseNamesForAppleFramework = false // false by default
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}
