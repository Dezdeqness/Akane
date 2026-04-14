package com.dezdeqness.akane

import android.app.Application
import com.dezdeqness.shared.di.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AkaneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AkaneApplication)
            modules(KoinModules.modules())
        }
    }
}
