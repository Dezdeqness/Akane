package com.dezdeqness.genre.di

import com.dezdeqness.catalog.ui.mapper.ReleaseUiMapper
import com.dezdeqness.genre.ui.all.AllGenresViewModel
import com.dezdeqness.genre.ui.mapper.GenreUiMapper
import com.dezdeqness.genre.ui.releases.GenreReleasesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val genreModule = module {
    includes(dataModule, domainModule)
    single { GenreUiMapper() }
    single { ReleaseUiMapper() }
    viewModel {
        AllGenresViewModel(
            genreRepository = get(),
            genreUiMapper = get(),
            coroutineDispatcherProvider = get(),
            errorReporter = get(),
        )
    }
    viewModel { (genreId: Int) ->
        GenreReleasesViewModel(
            genreId = genreId,
            genreRepository = get(),
            releaseUiMapper = get(),
            coroutineDispatcherProvider = get(),
            errorReporter = get(),
        )
    }
}
