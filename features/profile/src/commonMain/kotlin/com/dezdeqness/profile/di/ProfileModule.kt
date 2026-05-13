package com.dezdeqness.profile.di

import com.dezdeqness.profile.ui.ProfileViewModel
import com.dezdeqness.profile.ui.mapper.ProfileUiMapper
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    includes(dataModule, domainModule)
    single { ProfileUiMapper() }
    viewModelOf(::ProfileViewModel)
}
