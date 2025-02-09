plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
    alias(libs.plugins.dezdeqness.kmp.library)
}

android {
    namespace = "com.dezdeqness.shared"
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designsystem)
            implementation(projects.core.network)

            implementation(projects.features.feed)
            implementation(projects.features.details)
            implementation(projects.features.videoplayer)

            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(compose.ui)

            implementation(libs.androidx.navigation.compose)
            implementation(compose.components.uiToolingPreview)
        }
    }
}
