package com.dezdeqness.videoplayer.di

import com.dezdeqness.videoplayer.core.player.provider.VideoPlayerProvider
import com.dezdeqness.videoplayer.player.mpv.MpvLibraryLoader
import org.koin.dsl.module

actual val playerModule = module {
    // Warm libmpv off the UI thread at startup so opening the player screen doesn't stall.
    MpvLibraryLoader.preloadAsync()
    single { VideoPlayerProvider() }
}
