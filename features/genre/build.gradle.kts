plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
}

android {
    namespace = "com.dezdeqness.genre"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktorfit.common)
            implementation(libs.compose.material3)
            implementation(libs.material.icons.core)
            implementation(compose.components.resources)
            implementation(libs.kermit)
            implementation(libs.compottie)
            implementation(libs.compottie.dot)
            implementation(project(":common:analytics"))
            implementation(project(":shared:catalog-ui"))
            api(project(":contract:genre"))
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}
