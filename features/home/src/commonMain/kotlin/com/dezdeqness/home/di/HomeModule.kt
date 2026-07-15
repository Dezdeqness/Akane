package com.dezdeqness.home.di

import com.dezdeqness.home.domain.LoadHomeFeedUseCase
import com.dezdeqness.home.ui.HomeViewModel
import com.dezdeqness.home.ui.mapper.HomeUiMapper
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    single { HomeUiMapper() }
    single {
        LoadHomeFeedUseCase(
            promoRepository = get(),
            calendarRepository = get(),
            feedRepository = get(),
            franchiseRepository = get(),
            genreRepository = get(),
            coroutineDispatcherProvider = get(),
        )
    }
    viewModel {
        HomeViewModel(
            loadHomeFeedUseCase = get(),
            homeUiMapper = get(),
            errorReporter = get(),
            coroutineDispatcherProvider = get(),
        )
    }
}
