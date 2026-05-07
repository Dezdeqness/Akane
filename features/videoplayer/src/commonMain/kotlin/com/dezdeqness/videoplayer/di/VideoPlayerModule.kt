package com.dezdeqness.videoplayer.di

import com.dezdeqness.videoplayer.core.player.provider.VideoPlayerProvider
import com.dezdeqness.videoplayer.ui.MediaItemMapper
import com.dezdeqness.videoplayer.ui.VideoPlayerUiMapper
import com.dezdeqness.videoplayer.ui.VideoPlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val videoPlayerModule = module {
    includes(domainModule, playerModule)
    factory { get<VideoPlayerProvider>().create() }
    single { VideoPlayerUiMapper() }
    single { MediaItemMapper() }
    viewModel { (id: Long, episodeId: String, downloadReleaseId: Long, downloadStartEpisodeId: String) ->
        VideoPlayerViewModel(
            player = get(),
            releaseRepository = get(),
            downloadEpisodeRepository = get(),
            mediaItemMapper = get(),
            uiMapper = get(),
            dispatchers = get(),
            releaseId = id,
            initialEpisodeId = episodeId,
            downloadReleaseId = downloadReleaseId,
            downloadStartEpisodeId = downloadStartEpisodeId,
            analytics = get(),
            errorReporter = get(),
        )
    }
}
