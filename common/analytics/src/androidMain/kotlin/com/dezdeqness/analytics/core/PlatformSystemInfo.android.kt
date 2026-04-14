package com.dezdeqness.analytics.core

import android.os.Build
import java.util.Locale

actual fun readPlatformSystemInfo(): PlatformSystemInfo = PlatformSystemInfo(
    locale = Locale.getDefault().toLanguageTag(),
    osName = "Android",
    osVersion = Build.VERSION.RELEASE ?: "unknown",
    deviceModel = Build.MODEL ?: "unknown",
)
