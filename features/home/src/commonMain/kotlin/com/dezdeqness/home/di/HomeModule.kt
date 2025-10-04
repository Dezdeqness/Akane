package com.dezdeqness.home.di

import com.dezdeqness.home.ui.HomeViewModel
import com.dezdeqness.home.ui.mapper.HomeUiMapper
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    single { HomeUiMapper() }
    viewModel {
        HomeViewModel(
            feedRepository = get(),
            homeUiMapper = get(),
            coroutineDispatcherProvider = get(),
        )
    }
}
