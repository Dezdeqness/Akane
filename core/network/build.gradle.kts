import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
    id("de.jensklingenberg.ktorfit") version "2.2.0"
}

android {
    namespace = "com.dezdeqness.network"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvm("desktop")
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
            implementation(libs.bundles.ktor.common)
            implementation(libs.bundles.ktorfit.common)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            implementation(libs.ktor.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
    }

}

dependencies {
    add("kspCommonMainMetadata", libs.ktorfit.ksp)
    add("kspAndroid", libs.ktorfit.ksp)
    add("kspIosX64", libs.ktorfit.ksp)
    add("kspDesktop", libs.ktorfit.ksp)
    add("kspIosArm64", libs.ktorfit.ksp)
    add("kspIosSimulatorArm64", libs.ktorfit.ksp)
}
