plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.calendar.contract"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}
