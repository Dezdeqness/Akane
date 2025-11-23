package com.dezdeqness.feed.di

import com.dezdeqness.feed.ui.FeedUiMapper
import com.dezdeqness.feed.ui.FeedViewModel
import com.dezdeqness.feed.ui.filter.FeedFilterMapper
import com.dezdeqness.feed.ui.filter.FeedFilterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val feedModule = module {
    includes(dataModule, domainModule)
    single { FeedUiMapper() }
    single { FeedFilterMapper(genreProvider = get()) }
    viewModel {
        FeedViewModel(
            feedRepository = get(),
            feedUiMapper = get(),
            coroutineDispatcherProvider = get(),
        )
    }
    viewModel {
        FeedFilterViewModel(
            feedFilterMapper = get(),
        )
    }
}
