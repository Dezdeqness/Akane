package com.dezdeqness.auth.di

import com.dezdeqness.auth.contract.repository.AuthRepository
import com.dezdeqness.auth.data.repository.AuthRepositoryImpl
import org.koin.dsl.module

internal val domainModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(
            authApiDatasource = get(),
        )
    }
}
