package com.dezdeqness.videoplayer.di

import com.dezdeqness.videoplayer.core.player.provider.VideoPlayerProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val playerModule = module {
    single { VideoPlayerProvider(androidContext()) }
}
