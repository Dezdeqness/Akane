
plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

android {
    namespace = "com.dezdeqness.designsystem"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.material.icons.core)
            implementation(compose.components.resources)
            api(libs.compose.ui.tooling.preview)

            api(libs.core.ui)

            implementation(libs.coil.kt)
            implementation(libs.coil.network.ktor)
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }

}
