package com.dezdeqness.stories.di

import com.dezdeqness.stories.ui.StoriesUiMapper
import com.dezdeqness.stories.ui.StoriesViewModel
import com.dezdeqness.stories.ui.favourite.StoryFavouriteViewModel
import com.dezdeqness.stories.ui.player.StoryPlayerPool
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val storiesModule = module {
    includes(dataModule, domainModule)
    single { StoriesUiMapper() }
    factory { StoryPlayerPool(playerProvider = get()) }
    viewModel { (initialCollectionIndex: Int) ->
        StoriesViewModel(
            pool = get(),
            initialCollectionIndex = initialCollectionIndex,
            storiesRepository = get(),
            uiMapper = get(),
            dispatchers = get(),
            errorReporter = get(),
        )
    }
    viewModel {
        StoryFavouriteViewModel(
            personalRepository = get(),
            sessionManager = get(),
            dispatchers = get(),
            errorReporter = get(),
        )
    }
}
