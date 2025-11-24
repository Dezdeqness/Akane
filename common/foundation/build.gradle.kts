plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

android {
    namespace = "com.dezdeqness.foundation"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(libs.androidx.lifecycle.runtime.compose)
            api(libs.core)
        }
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }

    }

}
