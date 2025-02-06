plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
}

android {
    namespace = "com.dezdeqness.feed"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktor.common)
            api(libs.bundles.ktorfit.common)
            implementation(projects.core.network)
            implementation(projects.core.designsystem)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.core)
            implementation(libs.coil.kt)
            implementation(libs.coil.network.ktor)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.compose.material3)
            implementation(libs.kermit)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }

}
