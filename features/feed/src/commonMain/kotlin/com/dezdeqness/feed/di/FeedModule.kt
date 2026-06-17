package com.dezdeqness.feed.di

import com.dezdeqness.catalog.ui.mapper.ReleaseUiMapper
import com.dezdeqness.feed.ui.FeedViewModel
import com.dezdeqness.feed.ui.filter.FeedFilterMapper
import com.dezdeqness.feed.ui.filter.FeedFilterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val feedModule = module {
    includes(dataModule, domainModule)
    single { ReleaseUiMapper() }
    single { FeedFilterMapper(genreProvider = get()) }
    viewModel {
        FeedViewModel(
            feedRepository = get(),
            releaseUiMapper = get(),
            coroutineDispatcherProvider = get(),
            analytics = get(),
            errorReporter = get(),
        )
    }
    viewModel {
        FeedFilterViewModel(
            feedFilterMapper = get(),
        )
    }
}
