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
val appProperties = Properties().apply {
    load(rootProject.file("app.properties").inputStream())
}

val aptabaseAppKey = props["aptabase.app.key"] as? String
    ?: System.getenv("APTABASE_APP_KEY")
    ?: ""
val applicationVersion = appProperties.getProperty("app.version").orEmpty()
val applicationVersionCode = appProperties.getProperty("app.versionCode").orEmpty()
val sentryDsn = appProperties.getProperty("sentry.dsn").orEmpty()
val sentryEnvironment = appProperties.getProperty("sentry.environment", "production").orEmpty()
val sentrySampleRate = appProperties.getProperty("sentry.sampleRate", "1.0").orEmpty()
val sentryRelease = "com.dezdeqness.akane@$applicationVersion+$applicationVersionCode"

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
        buildConfigField(
            type = STRING,
            name = "APP_VERSION",
            value = applicationVersion,
            const = true,
        )
        buildConfigField(
            type = STRING,
            name = "SENTRY_DSN",
            value = sentryDsn,
            const = true,
        )
        buildConfigField(
            type = STRING,
            name = "SENTRY_ENVIRONMENT",
            value = sentryEnvironment,
            const = true,
        )
        buildConfigField(
            type = STRING,
            name = "SENTRY_RELEASE",
            value = sentryRelease,
            const = true,
        )
        buildConfigField(
            type = STRING,
            name = "SENTRY_SAMPLE_RATE",
            value = sentrySampleRate,
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
            implementation(libs.sentry.kotlin.multiplatform)
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
