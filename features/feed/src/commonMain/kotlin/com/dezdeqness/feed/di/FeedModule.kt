package com.dezdeqness.feed.di

import com.dezdeqness.feed.ui.FeedFilterMapper
import com.dezdeqness.feed.ui.FeedFilterViewModel
import com.dezdeqness.feed.ui.FeedUiMapper
import com.dezdeqness.feed.ui.FeedViewModel
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
