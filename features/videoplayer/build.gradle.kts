plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
}

android {
    namespace = "com.dezdeqness.videoplayer"
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktorfit.common)
            implementation(libs.coil.compose)
            implementation(libs.coil.core)
            implementation(libs.coil.kt)
            implementation(libs.coil.network.ktor)
            implementation(libs.compose.material3)
            implementation(libs.kermit)
            implementation(compose.components.resources)
            implementation(project(":common:analytics"))

            implementation(project(":contract:release"))
            implementation(project(":contract:downloads"))
            implementation(project(":contract:views"))
        }

        androidMain.dependencies {
            implementation(libs.media3.ui)
            implementation(libs.media3.common)
            implementation(libs.media3.exoplayer)
            implementation(libs.media3.hls)
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.vlcj)
        }
    }

}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
}
