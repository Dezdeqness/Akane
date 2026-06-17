package com.dezdeqness.genre.di

import com.dezdeqness.genre.contract.repository.GenreRepository
import com.dezdeqness.genre.data.repository.GenreRepositoryImpl
import org.koin.dsl.module

val domainModule = module {
    single<GenreRepository> {
        GenreRepositoryImpl(
            genreApiDatasource = get(),
        )
    }
}
