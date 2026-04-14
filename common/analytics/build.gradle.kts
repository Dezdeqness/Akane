plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.dezdeqness.analytics"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.bundles.ktor.common)
            implementation(libs.kermit)
        }

        androidMain.dependencies {
            implementation(libs.ktor.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(libs.ktor.okhttp)
        }
    }
}
