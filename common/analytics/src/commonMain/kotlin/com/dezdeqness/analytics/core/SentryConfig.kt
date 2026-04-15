package com.dezdeqness.analytics.core

data class SentryConfig(
    val dsn: String,
    val environment: String,
    val release: String,
    val sampleRate: Double,
    val isDebug: Boolean,
)
