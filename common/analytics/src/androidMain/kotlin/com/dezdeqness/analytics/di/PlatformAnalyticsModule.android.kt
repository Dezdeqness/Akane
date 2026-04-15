package com.dezdeqness.analytics.di

import android.content.Context
import android.content.pm.ApplicationInfo
import com.dezdeqness.analytics.core.AptabaseConfig
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformAnalyticsModule(): Module = module {
    single {
        val context: Context = get()

        AptabaseConfig(
            appKey = AptabaseSecrets.APTABASE_APP_KEY,
            appVersion = AptabaseSecrets.APP_VERSION,
            isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
        )
    }
}
