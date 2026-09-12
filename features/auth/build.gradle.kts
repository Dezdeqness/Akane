plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.dezdeqness.auth"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktorfit.common)
            implementation(libs.compose.material3)
            implementation(libs.material.icons.core)
            implementation(libs.kermit)
            implementation(project(":common:analytics"))
            api(project(":contract:auth"))
            implementation(project(":common:network"))

            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
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
