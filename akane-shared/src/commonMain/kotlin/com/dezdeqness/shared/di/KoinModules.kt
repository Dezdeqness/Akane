package com.dezdeqness.shared.di

import com.dezdeqness.details.di.detailsModule
import com.dezdeqness.feed.di.feedModule
import com.dezdeqness.personal.di.personalModule
import com.dezdeqness.videoplayer.di.videoPlayerModule
import org.koin.core.context.startKoin

object KoinModules {

    val modules = listOf(
        feedModule,
        detailsModule,
        videoPlayerModule,
        personalModule,
    )

    fun initKoinModules() {
        startKoin {
            modules(modules)
        }
    }

}
