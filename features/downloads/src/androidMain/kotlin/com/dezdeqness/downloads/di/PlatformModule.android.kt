package com.dezdeqness.downloads.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.dezdeqness.downloads.data.manager.engine.DownloadEngine
import com.dezdeqness.downloads.data.manager.engine.Media3DownloadEngine
import com.dezdeqness.downloads.data.platform.DownloadDirectoryProvider
import com.dezdeqness.downloads.notification.AndroidDownloadNotifier
import com.dezdeqness.downloads.notification.DownloadNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.Executors
import androidx.media3.exoplayer.offline.DownloadManager as Media3DownloadManager

@OptIn(UnstableApi::class)
actual fun platformModule(): Module = module {
    single { DownloadDirectoryProvider(context = get()) }
    single<DownloadNotifier> { AndroidDownloadNotifier(context = get()) }

    single<DatabaseProvider> { StandaloneDatabaseProvider(get<Context>()) }

    single<Cache> {
        val cacheDir = File(get<DownloadDirectoryProvider>().getDownloadDirectory(), "media3_cache")
        SimpleCache(cacheDir, NoOpCacheEvictor(), get<DatabaseProvider>())
    }

    single {
        Media3DownloadManager(
            get<Context>(),
            get<DatabaseProvider>(),
            get<Cache>(),
            OkHttpDataSource.Factory(okhttp3.OkHttpClient()),
            Executors.newFixedThreadPool(DOWNLOAD_EXECUTOR_THREADS),
        )
    }

    single<DownloadEngine> {
        Media3DownloadEngine(
            context = get(),
            media3Manager = get(),
            downloadEpisodeRepository = get(),
            syncRepository = get(),
            fileManager = get(),
            eventDispatcher = get(),
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
        )
    }
}

private const val DOWNLOAD_EXECUTOR_THREADS = 12
