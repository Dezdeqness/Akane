plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.franchise.contract"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":contract:catalog"))
        }
    }
}
