import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    alias(libs.plugins.dezdeqness.kmp.library)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.kotlin.serialization)
}

val props = Properties().apply {
    val file = rootProject.file("local.properties")
    load(file.inputStream())
}

val aptabaseAppKey = props["aptabase.app.key"] as? String
    ?: System.getenv("APTABASE_APP_KEY")
    ?: ""

android {
    namespace = "com.dezdeqness.analytics"
}

buildkonfig {
    packageName = "com.dezdeqness.analytics.di"
    objectName = "AptabaseSecrets"

    defaultConfigs {
        buildConfigField(
            type = STRING,
            name = "APTABASE_APP_KEY",
            value = aptabaseAppKey,
            const = true,
        )
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.bundles.ktor.common)
            implementation(libs.kermit)
        }

        androidMain.dependencies {
            implementation(libs.ktor.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(libs.ktor.okhttp)
        }
    }
}
