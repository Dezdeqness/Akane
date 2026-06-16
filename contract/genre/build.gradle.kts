plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.genre.contract"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":contract:feed"))
        }
    }
}
