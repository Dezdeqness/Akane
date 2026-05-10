plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.auth.contract"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}
