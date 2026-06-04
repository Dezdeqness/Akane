package com.dezdeqness.personal.di

import com.dezdeqness.network.services.FavoritesService
import com.dezdeqness.personal.data.datasource.PersonalLocalDatasource
import com.dezdeqness.personal.data.datasource.PersonalRemoteDatasource
import com.dezdeqness.personal.data.datasource.impl.PersonalLocalDatasourceImpl
import com.dezdeqness.personal.data.datasource.impl.PersonalRemoteDatasourceImpl
import com.dezdeqness.personal.data.db.PersonalDatabase
import com.dezdeqness.personal.data.mapper.PersonalMapper
import org.koin.dsl.module

val dataModule = module {
    single { PersonalMapper() }
    single<PersonalLocalDatasource> {
        PersonalLocalDatasourceImpl(
            personalDao = get<PersonalDatabase>().personalDao(),
        )
    }
    single<PersonalRemoteDatasource> {
        PersonalRemoteDatasourceImpl(
            favoritesService = get<FavoritesService>(),
            personalMapper = get(),
            errorMapper = get(),
        )
    }
}
