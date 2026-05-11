package com.dezdeqness.auth.di

import com.dezdeqness.auth.data.datasource.AuthApiDatasource
import com.dezdeqness.auth.data.datasource.impl.AuthApiDatasourceImpl
import com.dezdeqness.auth.data.mapper.AuthMapper
import org.koin.dsl.module

internal val dataModule = module {
    single { AuthMapper() }
    single<AuthApiDatasource> {
        AuthApiDatasourceImpl(
            authService = get(),
            authMapper = get(),
        )
    }
}
