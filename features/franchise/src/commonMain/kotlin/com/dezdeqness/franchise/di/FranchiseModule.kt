package com.dezdeqness.franchise.di

import com.dezdeqness.catalog.ui.mapper.ReleaseUiMapper
import com.dezdeqness.franchise.ui.all.AllFranchisesViewModel
import com.dezdeqness.franchise.ui.detail.FranchiseDetailViewModel
import com.dezdeqness.franchise.ui.mapper.FranchiseHeaderUiMapper
import com.dezdeqness.franchise.ui.mapper.FranchiseUiMapper
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val franchiseModule = module {
    includes(dataBaseModule(), dataModule, domainModule)
    single { FranchiseUiMapper() }
    single { FranchiseHeaderUiMapper() }
    single { ReleaseUiMapper() }
    viewModel {
        AllFranchisesViewModel(
            franchiseRepository = get(),
            franchiseUiMapper = get(),
            coroutineDispatcherProvider = get(),
            errorReporter = get(),
        )
    }
    viewModel { (franchiseId: String) ->
        FranchiseDetailViewModel(
            franchiseId = franchiseId,
            franchiseRepository = get(),
            releaseUiMapper = get(),
            franchiseHeaderUiMapper = get(),
            coroutineDispatcherProvider = get(),
            errorReporter = get(),
        )
    }
}
