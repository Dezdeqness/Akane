package com.dezdeqness.personal.di

import com.dezdeqness.personal.data.datasource.PersonalDatasource
import com.dezdeqness.personal.data.datasource.impl.PersonalDatasourceImpl
import com.dezdeqness.personal.data.db.PersonalDatabase
import com.dezdeqness.personal.data.mapper.PersonalMapper
import org.koin.dsl.module

val dataModule = module {
    single { PersonalMapper() }
    single<PersonalDatasource> {
        PersonalDatasourceImpl(
            personalDao = get<PersonalDatabase>().personalDao(),
            personalMapper = get(),
        )
    }
}
