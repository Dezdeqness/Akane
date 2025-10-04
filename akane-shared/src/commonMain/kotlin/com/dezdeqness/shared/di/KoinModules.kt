package com.dezdeqness.shared.di

import com.dezdeqness.foundation.di.coreModule
import com.dezdeqness.details.di.detailsModule
import com.dezdeqness.feed.di.feedModule
import com.dezdeqness.home.di.homeModule
import com.dezdeqness.personal.di.personalModule
import com.dezdeqness.videoplayer.di.videoPlayerModule
import org.koin.core.context.startKoin

object KoinModules {

    val modules = listOf(
        coreModule,
        feedModule,
        detailsModule,
        videoPlayerModule,
        personalModule,
        homeModule,
    )

    fun initKoinModules() {
        startKoin {
            modules(modules)
        }
    }

}
