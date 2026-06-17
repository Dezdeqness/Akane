package com.dezdeqness.genre.di

import com.dezdeqness.genre.data.datasource.GenreApiDatasource
import com.dezdeqness.genre.data.datasource.impl.GenreApiDatasourceImpl
import com.dezdeqness.genre.data.mapper.GenreMapper
import com.dezdeqness.genre.data.mapper.GenreReleaseMapper
import org.koin.dsl.module

val dataModule = module {
    single { GenreMapper() }
    single { GenreReleaseMapper() }
    single<GenreApiDatasource> {
        GenreApiDatasourceImpl(
            genreService = get(),
            genreMapper = get(),
            genreReleaseMapper = get(),
            errorMapper = get(),
        )
    }
}
