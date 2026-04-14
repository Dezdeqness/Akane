package com.dezdeqness.analytics.core

import java.util.Locale

actual fun readPlatformSystemInfo(): PlatformSystemInfo = PlatformSystemInfo(
    locale = Locale.getDefault().toLanguageTag(),
    osName = System.getProperty("os.name") ?: "Desktop",
    osVersion = System.getProperty("os.version") ?: "unknown",
    deviceModel = System.getProperty("os.arch") ?: "unknown",
)
