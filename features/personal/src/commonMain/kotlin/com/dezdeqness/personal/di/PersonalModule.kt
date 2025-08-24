package com.dezdeqness.personal.di

import com.dezdeqness.personal.ui.PersonalViewModel
import com.dezdeqness.personal.ui.mapper.PersonalUiMapper
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val personalModule = module {
    includes(dataBaseModule(), dataModule, domainModule)
    single { PersonalUiMapper() }
    viewModel {
        PersonalViewModel(
            personalRepository = get(),
            personalUiMapper = get(),
        )
    }
}
