package com.dezdeqness.videoplayer.di

import com.dezdeqness.videoplayer.ui.VideoPlayerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val videoPlayerModule = module {
    includes(domainModule)
    viewModelOf(::VideoPlayerViewModel)
}
