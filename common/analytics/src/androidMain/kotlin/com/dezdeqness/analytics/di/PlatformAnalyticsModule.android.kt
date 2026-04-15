package com.dezdeqness.analytics.di

import android.content.Context
import android.content.pm.ApplicationInfo
import com.dezdeqness.analytics.core.AptabaseConfig
import com.dezdeqness.analytics.core.SentryConfig
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformAnalyticsModule(): Module = module {
    single {
        val context: Context = get()
        val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        AptabaseConfig(
            appKey = AptabaseSecrets.APTABASE_APP_KEY,
            appVersion = AptabaseSecrets.APP_VERSION,
            isDebug = isDebug,
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
