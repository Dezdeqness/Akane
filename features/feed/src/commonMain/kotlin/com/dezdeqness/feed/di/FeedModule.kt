package com.dezdeqness.feed.di

import com.dezdeqness.feed.ui.FeedUiMapper
import com.dezdeqness.feed.ui.FeedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val feedModule = module {
    includes(domainModule)
    single { FeedUiMapper() }
    viewModel {
        FeedViewModel(
            feedRepository = get(),
            feedUiMapper = get(),
        )
    }
}
