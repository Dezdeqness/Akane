package com.dezdeqness.details.di

import com.dezdeqness.details.data.datasource.ReleaseApiDatasource
import com.dezdeqness.details.data.datasource.impl.ReleaseApiDatasourceImpl
import com.dezdeqness.details.data.mapper.EpisodesManager
import com.dezdeqness.details.data.mapper.ReleaseMapper
import com.dezdeqness.details.data.repository.ReleaseRepositoryImpl
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.network.di.networkModule
import org.koin.dsl.module

val domainModule = module {
    includes(networkModule)
    single { EpisodesManager() }
    single { ReleaseMapper(get()) }
    single<ReleaseApiDatasource> {
        ReleaseApiDatasourceImpl(
            catalogService = get(),
            releaseMapper = get(),
        )
    }
    single<ReleaseRepository> {
        ReleaseRepositoryImpl(releaseApiDatasource = get())
    }
}
