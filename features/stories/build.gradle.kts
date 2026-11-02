plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
}

android {
    namespace = "com.dezdeqness.stories"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktorfit.common)
            implementation(libs.compose.material3)
            implementation(libs.material.icons.core)
            implementation(libs.kermit)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(project(":common:analytics"))
            implementation(project(":common:foundation"))
            api(project(":contract:stories"))
            implementation(project(":contract:personal"))
            implementation(project(":contract:auth"))
            implementation(project(":contract:release"))
            implementation(project(":features:videoplayer"))
        }
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}
