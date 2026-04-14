package com.dezdeqness.analytics.di

import com.dezdeqness.analytics.core.AptabaseConfig
import org.koin.core.module.Module
import org.koin.dsl.module

private const val APP_VERSION = "1.1.0"

internal actual fun platformAnalyticsModule(): Module = module {
    single {
        AptabaseConfig(
            appKey = AptabaseSecrets.APTABASE_APP_KEY,
            appVersion = APP_VERSION,
            // Intended
            isDebug = false,
        )
    }
}
