plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.personal.contract"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
        }
    }
}
