package com.dezdeqness.details.di

import com.dezdeqness.details.ui.EpisodesUiMapper
import com.dezdeqness.details.ui.ReleaseDetailsUiMapper
import com.dezdeqness.details.ui.ReleaseDetailsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val detailsModule = module {
    includes(dataModule, domainModule)
    single { EpisodesUiMapper() }
    single { ReleaseDetailsUiMapper(episodesUiMapper = get()) }
    viewModelOf(::ReleaseDetailsViewModel)
}
