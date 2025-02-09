package com.dezdeqness.shared.di

import com.dezdeqness.details.di.detailsModule
import com.dezdeqness.feed.di.feedModule
import org.koin.core.context.startKoin

object KoinModules {

    val modules = listOf(
        feedModule,
        detailsModule
    )

    fun initKoinModules() {
        startKoin {
            modules(modules)
        }
    }

}
