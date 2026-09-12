plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.dezdeqness.details"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktorfit.common)
            implementation(libs.compose.material3)
            implementation(libs.material.icons.core)
            implementation(libs.kermit)
            implementation(libs.compottie)
            implementation(libs.compottie.dot)
            implementation(project(":common:analytics"))
            implementation(project(":common:foundation"))
            api(project(":contract:release"))
            implementation(project(":contract:personal"))
            implementation(project(":contract:auth"))
            implementation(project(":contract:downloads"))
            implementation(project(":contract:views"))
            implementation(project(":features:downloads"))
        }
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        val desktopTest by getting

        desktopTest.dependencies {
            implementation(project(":common:screenshot-testing"))
            implementation(libs.roborazzi.compose.desktop)
            implementation(libs.compose.ui.test)
            implementation(compose.desktop.currentOs)
            implementation(kotlin("test"))
        }
    }

}
