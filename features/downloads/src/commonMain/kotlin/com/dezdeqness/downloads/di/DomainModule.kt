package com.dezdeqness.downloads.di

import com.dezdeqness.downloads.data.repository.DownloadEpisodeRepositoryImpl
import com.dezdeqness.downloads.data.repository.SyncDownloadsEpisodeRepositoryImpl
import com.dezdeqness.downloads.domain.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.domain.repository.SyncDownloadsEpisodeRepository
import org.koin.dsl.module

internal val domainModule = module {
    single<DownloadEpisodeRepository> {
        DownloadEpisodeRepositoryImpl(
            downloadEpisodeDao = get(),
            syncDownloadEpisodeDao = get(),
            downloadMapper = get(),
        )
    }
    single<SyncDownloadsEpisodeRepository> {
        SyncDownloadsEpisodeRepositoryImpl(
            downloadEpisodeRepository = get(),
            syncDownloadEpisodeDao = get(),
        )
    }
}
