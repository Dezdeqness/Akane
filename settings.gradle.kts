import java.io.FileInputStream
import java.util.Properties

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

var githubUsername: String
var githubToken: String

val localPropsFile = file("local.properties")
if (localPropsFile.exists()) {
    val props = Properties()
    props.load(FileInputStream(localPropsFile))
    githubUsername = props.getProperty("github.username")
    githubToken = props.getProperty("github.token")
} else {
    githubUsername = System.getenv("USERNAME") ?: ""
    githubToken = System.getenv("TOKEN") ?: ""
}

println("GithubUsername is: '$githubUsername'")
println("GithubToken is: '${githubToken.take(4)}...'")


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
        maven {
            url = uri("https://maven.pkg.github.com/Dezdeqness/Android-Support-Things")
            credentials {
                username = githubUsername
                password = githubToken
            }
        }
    }
}

include(":akane-android")
include(":akane-desktop")
include(":akane-shared")

include(":common:network")
include(":common:designsystem")
include(":common:core")

include(":features:feed")
include(":features:details")
include(":features:videoplayer")
include(":features:personal")
include(":features:home")
