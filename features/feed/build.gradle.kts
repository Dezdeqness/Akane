plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.dezdeqness.feed"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktorfit.common)
            implementation(libs.compose.material3)
            implementation(libs.kermit)
            implementation(libs.compottie)
            implementation(libs.compottie.dot)
            implementation(libs.kotlinx.serialization.json)
            implementation(project(":common:cache"))
            implementation(project(":common:foundation"))
            implementation(project(":common:analytics"))
            implementation(project(":shared:catalog-ui"))
            api(project(":contract:feed"))
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }

}
