package com.dezdeqness.downloads.di

import com.dezdeqness.downloads.data.repository.DownloadEpisodeRepositoryImpl
import com.dezdeqness.downloads.data.repository.SyncDownloadsEpisodeRepositoryImpl
import com.dezdeqness.downloads.domain.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.domain.repository.SyncDownloadsEpisodeRepository
import com.dezdeqness.downloads.domain.usecase.CancelAllDownloadsUseCase
import com.dezdeqness.downloads.domain.usecase.CancelDownloadUseCase
import com.dezdeqness.downloads.domain.usecase.EnqueueDownloadUseCase
import org.koin.core.module.dsl.factoryOf
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
    factoryOf(::EnqueueDownloadUseCase)
    factoryOf(::CancelDownloadUseCase)
    factoryOf(::CancelAllDownloadsUseCase)
}
