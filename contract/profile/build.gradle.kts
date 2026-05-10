plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.profile.contract"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}
