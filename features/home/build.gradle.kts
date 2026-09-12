plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.dezdeqness.home"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktorfit.common)
            implementation(libs.coil.compose)
            implementation(libs.compose.material3)
            implementation(compose.components.resources)
            implementation(libs.kermit)
            implementation(project(":common:cache"))
            implementation(project(":common:analytics"))
            implementation(project(":contract:feed"))
            implementation(project(":contract:calendar"))
            implementation(project(":contract:genre"))
            implementation(project(":contract:promo"))
            implementation(project(":contract:franchise"))
            implementation(project(":contract:downloads"))
            implementation(project(":contract:views"))

            implementation(libs.compottie)
            implementation(libs.compottie.dot)
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
