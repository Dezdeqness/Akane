package com.dezdeqness.analytics.di

import com.dezdeqness.analytics.core.AptabaseConfig
import com.dezdeqness.analytics.core.SentryConfig
import kotlin.experimental.ExperimentalNativeApi
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
internal actual fun platformAnalyticsModule(): Module = module {
    single {
        AptabaseConfig(
            appKey = AptabaseSecrets.APTABASE_APP_KEY,
            appVersion = AptabaseSecrets.APP_VERSION,
            isDebug = Platform.isDebugBinary,
        )
    }
    single {
        SentryConfig(
            dsn = AptabaseSecrets.SENTRY_DSN,
            environment = AptabaseSecrets.SENTRY_ENVIRONMENT,
            release = AptabaseSecrets.SENTRY_RELEASE,
            sampleRate = AptabaseSecrets.SENTRY_SAMPLE_RATE.toDoubleOrNull() ?: 1.0,
            isDebug = get<AptabaseConfig>().isDebug,
        )
    }
}
