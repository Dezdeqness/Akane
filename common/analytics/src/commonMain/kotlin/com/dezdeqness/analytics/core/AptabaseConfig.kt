package com.dezdeqness.analytics.core

data class AptabaseConfig(
    val appKey: String,
    val appVersion: String,
    val isDebug: Boolean,
    val host: String? = null,
    val flushIntervalMillis: Long = if (isDebug) 2_000L else 60_000L,
    val sdkVersion: String = DEFAULT_SDK_VERSION,
) {
    fun resolvedHost(): String {
        val configuredHost = host?.trim()?.trimEnd('/')
        if (!configuredHost.isNullOrEmpty()) {
            return configuredHost
        }

        return if (appKey.uppercase().contains("-EU-")) {
            "https://eu.aptabase.com"
        } else {
            "https://us.aptabase.com"
        }
    }

    companion object {
        const val DEFAULT_SDK_VERSION = "aptabase-kmp-core@0.1.0"
    }
}