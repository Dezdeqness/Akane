plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.dezdeqness.foundation"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.okio)
            api(libs.core)
            api(libs.kotlinx.serialization.json)
        }
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }

    }

}
