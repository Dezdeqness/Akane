package com.dezdeqness.analytics.core

typealias AnalyticsProperties = Map<String, AnalyticsValue>

interface Analytics {
    fun track(eventName: String, properties: AnalyticsProperties = emptyMap())

    suspend fun flush()
}
