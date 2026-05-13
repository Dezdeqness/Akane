plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
}

android {
    namespace = "com.dezdeqness.profile"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktorfit.common)
            implementation(libs.compose.material3)
            implementation(libs.material.icons.core)
            implementation(libs.kermit)
            implementation(libs.kotlinx.datetime)
            implementation(project(":common:analytics"))
            api(project(":contract:profile"))
            api(project(":contract:auth"))
            implementation(project(":common:network"))
            implementation(project(":features:auth"))
        }
    }
}
