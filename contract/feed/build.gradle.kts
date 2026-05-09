plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.feed.contract"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}
