
plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
    alias(libs.plugins.kotlin.serialization)
    id("de.jensklingenberg.ktorfit") version "2.2.0"
}

android {
    namespace = "com.dezdeqness.network"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.ktor.common)
            implementation(libs.bundles.ktorfit.common)
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
