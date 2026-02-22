package com.dezdeqness.videoplayer.di

import com.dezdeqness.videoplayer.core.player.provider.VideoPlayerProvider
import com.dezdeqness.videoplayer.ui.VideoPlayerUiMapper
import com.dezdeqness.videoplayer.ui.VideoPlayerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val videoPlayerModule = module {
    includes(domainModule, playerModule)
    factory { get<VideoPlayerProvider>().create() }
    single { VideoPlayerUiMapper() }
    viewModelOf(::VideoPlayerViewModel)
}
