import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop") {
        withJava()
    }
    jvmToolchain(21)

    sourceSets {
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(projects.akaneShared)

            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.dezdeqness.akane.Akane"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.dezdeqness.akane"
            packageVersion = "1.0.0"
            includeAllModules = true

            windows {
                menuGroup = "com.dezdeqness.akane"
                shortcut = true
                dirChooser = true
                perUserInstall = true
            }
            macOS {
                bundleID = "com.dezdeqness.akane"
                dockName = "Akane"
            }
        }
    }
}
