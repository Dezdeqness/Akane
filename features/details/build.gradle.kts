import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

android {
    namespace = "com.dezdeqness.details"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

//val ktorfitVersion = "2.2.0"

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktor.common)
            api(libs.bundles.ktorfit.common)
            implementation(projects.core.network)
            implementation(projects.core.designsystem)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.core)
            implementation(libs.coil.kt)
            implementation(libs.coil.network.ktor)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.compose.material3)
            implementation(libs.kermit)
        }

    }

}
