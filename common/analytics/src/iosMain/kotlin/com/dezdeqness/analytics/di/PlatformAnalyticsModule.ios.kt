package com.dezdeqness.analytics.di

import com.dezdeqness.analytics.core.AptabaseConfig
import kotlin.experimental.ExperimentalNativeApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSBundle
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
internal actual fun platformAnalyticsModule(): Module = module {
    single {
        AptabaseConfig(
            appKey = AptabaseSecrets.APTABASE_APP_KEY,
            appVersion = NSBundle.mainBundle.infoDictionary
                ?.get("CFBundleShortVersionString") as? String ?: "1.2.0",
            isDebug = Platform.isDebugBinary,
        )
    }
}
