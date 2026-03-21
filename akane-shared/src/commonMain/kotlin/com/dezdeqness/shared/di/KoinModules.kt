package com.dezdeqness.shared.di

import com.dezdeqness.calendar.di.calendarModule
import com.dezdeqness.details.di.detailsModule
import com.dezdeqness.downloads.di.downloadsModule
import com.dezdeqness.feed.di.feedModule
import com.dezdeqness.foundation.di.coreModule
import com.dezdeqness.home.di.homeModule
import com.dezdeqness.network.di.networkModule
import com.dezdeqness.personal.di.personalModule
import com.dezdeqness.shared.AppViewModel
import com.dezdeqness.videoplayer.di.videoPlayerModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val appModule = module {
    viewModelOf(::AppViewModel)
}

object KoinModules {

    val modules = listOf(
        networkModule,
        coreModule,
        feedModule,
        detailsModule,
        videoPlayerModule,
        personalModule,
        homeModule,
        calendarModule,
        downloadsModule,
        appModule,
    )

    fun initKoinModules() {
        startKoin {
            modules(modules)
        }
    }

}
