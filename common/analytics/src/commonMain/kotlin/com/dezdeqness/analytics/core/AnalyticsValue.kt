package com.dezdeqness.analytics.core

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

sealed interface AnalyticsValue {
    fun toJsonElement(): JsonElement

    data class Text(val value: String) : AnalyticsValue {
        override fun toJsonElement(): JsonElement = JsonPrimitive(value)
    }

    data class Numeric(val value: Number) : AnalyticsValue {
        override fun toJsonElement(): JsonElement = when (value) {
            is Int -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            is Float -> JsonPrimitive(value)
            is Double -> JsonPrimitive(value)
            is Short -> JsonPrimitive(value.toInt())
            is Byte -> JsonPrimitive(value.toInt())
            else -> JsonPrimitive(value.toDouble())
        }
    }
}