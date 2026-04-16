package com.dezdeqness.analytics.di

import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.analytics.core.Analytics
import com.dezdeqness.analytics.AptabaseAnalytics
import com.dezdeqness.analytics.core.AptabaseConfig
import com.dezdeqness.analytics.DefaultAkaneAnalytics
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun analyticsModule() = module {
    includes(
        platformAnalyticsModule(),
        akaneErrorReporterModule(),
    )

    single {
        Json {
            encodeDefaults = true
            explicitNulls = false
        }
    }

    single<Analytics>(createdAtStart = true) {
        val config = get<AptabaseConfig>()
        AptabaseAnalytics(
            config = config,
            eventStore = get(),
        )
    }

    single<AkaneAnalytics> { DefaultAkaneAnalytics(analytics = get()) }
}
