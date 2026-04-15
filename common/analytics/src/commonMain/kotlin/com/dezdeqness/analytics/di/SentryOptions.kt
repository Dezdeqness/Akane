package com.dezdeqness.analytics.di

import com.dezdeqness.analytics.core.SentryConfig
import io.sentry.kotlin.multiplatform.SentryOptions

fun SentryOptions.applyAkaneConfig(config: SentryConfig) {
    dsn = config.dsn
    environment = config.environment
    release = config.release
    sampleRate = config.sampleRate
    debug = config.isDebug
    attachStackTrace = true
    attachThreads = true
    enableCaptureFailedRequests = true
    isAnrEnabled = true
    enableAppHangTracking = true
    enableAutoSessionTracking = true
}
