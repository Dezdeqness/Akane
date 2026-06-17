package com.dezdeqness.shared.di

import com.dezdeqness.analytics.di.analyticsModule
import com.dezdeqness.auth.di.authModule
import com.dezdeqness.calendar.di.calendarModule
import com.dezdeqness.details.di.detailsModule
import com.dezdeqness.downloads.di.downloadsModule
import com.dezdeqness.feed.di.feedModule
import com.dezdeqness.foundation.di.coreModule
import com.dezdeqness.genre.di.genreModule
import com.dezdeqness.home.di.homeModule
import com.dezdeqness.network.di.networkModule
import com.dezdeqness.personal.di.personalModule
import com.dezdeqness.profile.di.profileModule
import com.dezdeqness.auth.contract.session.SessionManager
import com.dezdeqness.shared.AppViewModel
import com.dezdeqness.shared.session.SessionManagerImpl
import com.dezdeqness.videoplayer.di.videoPlayerModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val appModule = module {
    single<SessionManager> {
        SessionManagerImpl(
            sessionTokenStore = get(),
            authRepository = get(),
            dispatcherProvider = get(),
        )
    }
    viewModelOf(::AppViewModel)
}

object KoinModules {

    fun modules(): List<Module> = buildList {
        add(analyticsModule())
        add(networkModule)
        add(coreModule)
        add(feedModule)
        add(detailsModule)
        add(videoPlayerModule)
        add(personalModule)
        add(homeModule)
        add(calendarModule)
        add(downloadsModule)
        add(authModule)
        add(profileModule)
        add(genreModule)
        add(appModule)
    }

    fun initKoinModules() {
        startKoin {
            modules(KoinModules.modules())
        }
    }

}
