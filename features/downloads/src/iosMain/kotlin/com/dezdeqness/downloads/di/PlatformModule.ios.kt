package com.dezdeqness.downloads.di

import com.dezdeqness.downloads.data.manager.engine.AvAssetDownloadEngine
import com.dezdeqness.downloads.data.manager.engine.DownloadEngine
import com.dezdeqness.downloads.data.platform.DownloadDirectoryProvider
import com.dezdeqness.downloads.notification.DownloadNotifier
import com.dezdeqness.downloads.notification.IosDownloadNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DownloadDirectoryProvider() }
    single<DownloadNotifier> { IosDownloadNotifier() }
    single<DownloadEngine> {
        AvAssetDownloadEngine(
            downloadEpisodeRepository = get(),
            syncRepository = get(),
            fileManager = get(),
            eventBus = get(),
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
        )
    }
}
