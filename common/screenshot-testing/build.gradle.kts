plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.dezdeqness.screenshot"
}

kotlin {
    sourceSets {
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(project(":common:designsystem"))
            implementation(project(":shared:catalog-ui"))
            implementation(compose.desktop.currentOs)
            implementation(libs.compose.material3)
            implementation(libs.coil.kt)
            implementation(libs.coil.compose)
            implementation(libs.coil.test)

            implementation(libs.roborazzi.compose.desktop)
            implementation(libs.compose.ui.test)
        }
    }
}
