package com.dezdeqness.auth.di

import com.dezdeqness.auth.data.datasource.AuthApiDatasource
import com.dezdeqness.auth.data.datasource.impl.AuthApiDatasourceImpl
import com.dezdeqness.auth.data.datastore.SessionTokenStore
import com.dezdeqness.auth.data.datastore.impl.PersistentSessionTokenManager
import com.dezdeqness.auth.data.mapper.AuthMapper
import com.dezdeqness.network.auth.TokenProvider
import org.koin.dsl.module

internal val dataModule = module {
    single { AuthMapper() }
    single<AuthApiDatasource> {
        AuthApiDatasourceImpl(
            authService = get(),
            authMapper = get(),
        )
    }
    single<SessionTokenStore> { PersistentSessionTokenManager(dataStore = get()) }
    single<TokenProvider> { get<SessionTokenStore>() }
}
