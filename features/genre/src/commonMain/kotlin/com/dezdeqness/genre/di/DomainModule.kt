package com.dezdeqness.genre.di

import com.dezdeqness.genre.contract.repository.GenreRepository
import com.dezdeqness.genre.data.cache.GenreCacheMapper
import com.dezdeqness.genre.data.repository.GenreRepositoryImpl
import org.koin.dsl.module

val domainModule = module {
    single { GenreCacheMapper() }
    single<GenreRepository> {
        GenreRepositoryImpl(
            genreApiDatasource = get(),
            jsonCacheStore = get(),
            genreCacheMapper = get(),
        )
    }
}
