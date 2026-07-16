package com.dezdeqness.details.di

import com.dezdeqness.details.ui.EpisodesUiMapper
import com.dezdeqness.details.ui.ReleaseDetailsUiMapper
import com.dezdeqness.details.ui.ReleaseDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val detailsModule = module {
    includes(dataModule, domainModule)
    single { EpisodesUiMapper(get()) }
    single { ReleaseDetailsUiMapper(episodesUiMapper = get()) }
    viewModel { (releaseId: Long) ->
        ReleaseDetailsViewModel(
            releaseId = releaseId,
            releaseRepository = get(),
            franchiseRepository = get(),
            personalRepository = get(),
            sessionManager = get(),
            downloadEpisodeRepository = get(),
            viewsRepository = get(),
            enqueueDownloadUseCase = get(),
            cancelDownloadUseCase = get(),
            cancelAllDownloadsUseCase = get(),
            releaseDetailsUiMapper = get(),
            coroutineDispatcherProvider = get(),
            analytics = get(),
            errorReporter = get(),
        )
    }
}
