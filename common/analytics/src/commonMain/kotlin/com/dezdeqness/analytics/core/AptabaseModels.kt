package com.dezdeqness.analytics.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AptabaseEventPayload(
    val timestamp: String,
    val sessionId: String,
    val eventName: String,
    val systemProps: AptabaseSystemProps,
    val props: Map<String, JsonElement> = emptyMap(),
)

@Serializable
data class AptabaseSystemProps(
    val locale: String,
    val osName: String,
    val osVersion: String,
    val deviceModel: String,
    val isDebug: Boolean,
    val appVersion: String,
    val sdkVersion: String,
)
