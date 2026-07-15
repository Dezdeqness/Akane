package com.dezdeqness.downloads.di

import com.dezdeqness.downloads.data.db.DownloadDatabase
import com.dezdeqness.downloads.data.hls.HlsParser
import com.dezdeqness.downloads.data.manager.DownloadFileManager
import com.dezdeqness.downloads.data.manager.DownloadManager
import com.dezdeqness.downloads.data.mapper.DownloadMapper
import com.dezdeqness.downloads.data.network.HlsDownloadService
import com.dezdeqness.downloads.data.network.createHlsDownloadService
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val dataModule = module {
    single(named("download")) { HttpClient() }

    single(named("downloadKtorfit")) {
        Ktorfit.Builder()
            .baseUrl("https://placeholder.local/")
            .httpClient(get<HttpClient>(named("download")))
            .build()
    }

    single<HlsDownloadService> {
        get<Ktorfit>(named("downloadKtorfit")).createHlsDownloadService()
    }
    single { get<DownloadDatabase>().downloadEpisodeDao() }
    single { get<DownloadDatabase>().syncDownloadEpisodeDao() }
    single { HlsParser() }
    single { DownloadMapper(fileManager = get()) }
    single {
        DownloadFileManager(
            downloadDirectoryProvider = get(),
        )
    }
    single {
        DownloadManager(
            hlsDownloadService = get(),
            hlsParser = get(),
            downloadEpisodeRepository = get(),
            syncRepository = get(),
            fileManager = get(),
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            coroutineDispatcherProvider = get(),
            analytics = get(),
            errorReporter = get(),
        )
    }
}
