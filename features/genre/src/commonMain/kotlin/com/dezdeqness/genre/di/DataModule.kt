package com.dezdeqness.genre.di

import com.dezdeqness.genre.data.datasource.GenreApiDatasource
import com.dezdeqness.genre.data.datasource.impl.GenreApiDatasourceImpl
import com.dezdeqness.genre.data.mapper.GenreMapper
import com.dezdeqness.genre.data.mapper.GenreReleaseMapper
import org.koin.dsl.module

val dataModule = module {
    single { GenreMapper(imageUrlBuilder = get()) }
    single { GenreReleaseMapper(imageUrlBuilder = get()) }
    single<GenreApiDatasource> {
        GenreApiDatasourceImpl(
            genreService = get(),
            genreMapper = get(),
            genreReleaseMapper = get(),
            errorMapper = get(),
        )
    }
}
