package com.dezdeqness.analytics.di

import com.dezdeqness.analytics.SentryAkaneErrorReporter
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.analytics.core.SentryConfig
import io.sentry.kotlin.multiplatform.Sentry
import org.koin.dsl.module

fun akaneErrorReporterModule() = module {
    single<AkaneErrorReporter>(createdAtStart = true) {
        val config = get<SentryConfig>()
        Sentry.init { options ->
            options.applyAkaneConfig(config)
        }
        SentryAkaneErrorReporter()
    }
}
