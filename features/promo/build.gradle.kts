plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.dezdeqness.promo"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktorfit.common)
            implementation(libs.compose.material3)
            implementation(libs.kermit)
            implementation(libs.kotlinx.serialization.json)
            implementation(project(":common:cache"))
            implementation(project(":common:foundation"))
            api(project(":contract:promo"))
        }
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}
