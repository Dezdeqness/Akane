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
            implementation(projects.common.analytics)

            implementation(compose.desktop.currentOs)
            implementation(libs.koin.core)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.dezdeqness.akane.Akane"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "Akane"
            packageVersion = "1.2.0"
            includeAllModules = true

            windows {
                menuGroup = "Akane"
                shortcut = true
                dirChooser = true
                perUserInstall = true

                iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
            }
            macOS {
                bundleID = "com.dezdeqness.akane"
                dockName = "Akane"

                iconFile.set(project.file("src/desktopMain/resources/icon.icns"))
            }
        }
    }
}
