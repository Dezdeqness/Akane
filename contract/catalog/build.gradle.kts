plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.catalog.contract"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}
