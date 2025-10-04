plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
}

android {
    namespace = "com.dezdeqness.core"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            api(libs.core)
        }
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

    }

}
