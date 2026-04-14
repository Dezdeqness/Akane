package com.dezdeqness.analytics.core

data class PlatformSystemInfo(
    val locale: String,
    val osName: String,
    val osVersion: String,
    val deviceModel: String,
)

expect fun readPlatformSystemInfo(): PlatformSystemInfo
