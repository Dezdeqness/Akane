package com.dezdeqness.analytics.di

import com.dezdeqness.analytics.core.AptabaseConfig
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformAnalyticsModule(): Module = module {
    single {
        AptabaseConfig(
            appKey = AptabaseSecrets.APTABASE_APP_KEY,
            appVersion = AptabaseSecrets.APP_VERSION,
            // Intended
            isDebug = false,
        )
    }
}
