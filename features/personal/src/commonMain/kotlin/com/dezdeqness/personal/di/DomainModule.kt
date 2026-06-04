package com.dezdeqness.personal.di

import com.dezdeqness.personal.data.repository.PersonalRepositoryImpl
import com.dezdeqness.personal.contract.repository.PersonalRepository
import org.koin.dsl.module

val domainModule = module {
    single<PersonalRepository> {
        PersonalRepositoryImpl(
            localDatasource = get(),
            remoteDatasource = get(),
        )
    }
}
