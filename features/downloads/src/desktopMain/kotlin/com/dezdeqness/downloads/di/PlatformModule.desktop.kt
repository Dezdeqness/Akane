package com.dezdeqness.downloads.di

import com.dezdeqness.downloads.data.manager.engine.DownloadEngine
import com.dezdeqness.downloads.data.manager.engine.KtorDownloadEngine
import com.dezdeqness.downloads.data.manager.pipeline.EpisodeDownloadPipeline
import com.dezdeqness.downloads.data.manager.pipeline.HlsPlaylistFetcher
import com.dezdeqness.downloads.data.manager.pipeline.SegmentDownloader
import com.dezdeqness.downloads.data.platform.DownloadDirectoryProvider
import com.dezdeqness.downloads.notification.DesktopTrayDownloadNotifier
import com.dezdeqness.downloads.notification.DownloadNotificationActions
import com.dezdeqness.downloads.notification.DownloadNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

actual fun platformModule() = module {
    single { DownloadDirectoryProvider() }
    single { DownloadNotificationActions() }
    single<DownloadNotifier> { DesktopTrayDownloadNotifier(actions = get()) }

    single {
        HlsPlaylistFetcher(
            hlsDownloadService = get(),
            hlsParser = get(),
        )
    }
    single {
        SegmentDownloader(
            hlsDownloadService = get(),
            fileManager = get(),
            coroutineDispatcherProvider = get(),
        )
    }
    single {
        EpisodeDownloadPipeline(
            playlistFetcher = get(),
            segmentDownloader = get(),
            fileManager = get(),
            syncRepository = get(),
            eventDispatcher = get(),
            analytics = get(),
        )
    }
    single<DownloadEngine> {
        KtorDownloadEngine(
            pipeline = get(),
            downloadEpisodeRepository = get(),
            syncRepository = get(),
            fileManager = get(),
            eventDispatcher = get(),
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            coroutineDispatcherProvider = get(),
            analytics = get(),
            errorReporter = get(),
        )
    }
}
