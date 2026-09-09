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
            implementation(compose.material3)
            implementation(libs.coil.kt)

            implementation(libs.roborazzi.compose.desktop)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}
