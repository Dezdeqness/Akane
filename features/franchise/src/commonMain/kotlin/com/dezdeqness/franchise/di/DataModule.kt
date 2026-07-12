package com.dezdeqness.franchise.di

import com.dezdeqness.franchise.data.cache.FranchiseCache
import com.dezdeqness.franchise.data.cache.FranchiseSnapshotMapper
import com.dezdeqness.franchise.data.datasource.FranchiseApiDatasource
import com.dezdeqness.franchise.data.datasource.impl.FranchiseApiDatasourceImpl
import com.dezdeqness.franchise.data.db.FranchiseDatabase
import com.dezdeqness.franchise.data.mapper.FranchiseDetailsMapper
import com.dezdeqness.franchise.data.mapper.FranchiseMapper
import com.dezdeqness.franchise.data.mapper.FranchiseReleaseMapper
import org.koin.dsl.module

val dataModule = module {
    single { get<FranchiseDatabase>().franchiseCacheDao() }
    single { FranchiseCache(franchiseCacheDao = get(), franchiseMapper = get()) }
    single { FranchiseSnapshotMapper() }
    single { FranchiseMapper(imageUrlBuilder = get()) }
    single { FranchiseReleaseMapper(imageUrlBuilder = get()) }
    single { FranchiseDetailsMapper(franchiseReleaseMapper = get(), imageUrlBuilder = get()) }
    single<FranchiseApiDatasource> {
        FranchiseApiDatasourceImpl(
            franchiseService = get(),
            franchiseMapper = get(),
            franchiseDetailsMapper = get(),
            errorMapper = get(),
        )
    }
}
