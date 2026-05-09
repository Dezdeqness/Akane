plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.release.contract"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}
