package com.dezdeqness.akane

import android.app.Application
import com.dezdeqness.feed.di.feedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AkaneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AkaneApplication)
            modules(feedModule)
        }
    }
}
