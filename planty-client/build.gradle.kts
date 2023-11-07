allprojects {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}

// https://stackoverflow.com/questions/46495539/how-do-i-replace-a-dependency-of-a-dependency-in-gradle

configurations.all {
    val conf = this
//    resolutionStrategy.force("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${libs.versions.coroutines.get()}")
    resolutionStrategy.eachDependency {
//        val isJvm = conf.name.contains("jvm", true)
        val details = this
//        val isGitliveDependency = details.requested.group == "dev.gitlive"
        if (details.requested.group == "org.jetbrains.kotlinx"
            && details.requested.name == "kotlinx-coroutines-core-jvm") {
            details.useTarget("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${libs.versions.coroutines.get()}")
        }
    }

    // Write code which replaces org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm with org.jetbrains.kotlinx:kotlinx-coroutines-swing
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.libres) apply false
}
