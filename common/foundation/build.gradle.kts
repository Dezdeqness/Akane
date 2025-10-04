plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.foundation"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            api(libs.core)
        }
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }

    }

}
