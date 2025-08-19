plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
}

android {
    namespace = "com.dezdeqness.videoplayer"
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        iosX64(),
    ).forEach { target ->
        target.compilations.getByName("main") {
            val nskeyvalueobserving by cinterops.creating
        }
    }

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
            implementation(compose.components.uiToolingPreview)

            implementation(project(":features:details"))
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
