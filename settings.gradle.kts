rootProject.name = "Akane"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
    }
}

include(":akane-android")
include(":akane-desktop")
include(":akane-shared")

include(":core:network")
include(":core:designsystem")

include(":features:feed")
include(":features:details")
include(":features:videoplayer")
include(":features:personal")
include(":features:home")
