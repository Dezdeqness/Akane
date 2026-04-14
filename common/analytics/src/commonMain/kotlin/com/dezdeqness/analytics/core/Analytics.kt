package com.dezdeqness.analytics.core

import com.dezdeqness.analytics.core.AnalyticsValue

typealias AnalyticsProperties = Map<String, AnalyticsValue>

interface Analytics {
    fun track(eventName: String, properties: AnalyticsProperties = emptyMap())

    suspend fun flush()
}
