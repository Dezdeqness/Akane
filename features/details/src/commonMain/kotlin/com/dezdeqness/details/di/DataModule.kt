package com.dezdeqness.details.di

import com.dezdeqness.details.data.datasource.FranchiseDatasource
import com.dezdeqness.details.data.datasource.ReleaseApiDatasource
import com.dezdeqness.details.data.datasource.impl.FranchiseDatasourceImpl
import com.dezdeqness.details.data.datasource.impl.ReleaseApiDatasourceImpl
import com.dezdeqness.details.data.mapper.EpisodesManager
import com.dezdeqness.details.data.mapper.FranchiseMapper
import com.dezdeqness.details.data.mapper.ReleaseMapper
import org.koin.dsl.module

val dataModule = module {
    single { EpisodesManager() }
    single { ReleaseMapper(get(), get()) }
    single { FranchiseMapper(get()) }
    single<ReleaseApiDatasource> {
        ReleaseApiDatasourceImpl(
            catalogService = get(),
            releaseMapper = get(),
            errorMapper = get(),
        )
    }
    single<FranchiseDatasource> {
        FranchiseDatasourceImpl(
            franchiseService = get(),
            franchiseMapper = get(),
            errorMapper = get(),
        )
    }
}
