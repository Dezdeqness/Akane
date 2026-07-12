package com.dezdeqness.franchise.di

import com.dezdeqness.franchise.contract.repository.FranchiseRepository
import com.dezdeqness.franchise.data.repository.FranchiseRepositoryImpl
import org.koin.dsl.module

val domainModule = module {
    single<FranchiseRepository> {
        FranchiseRepositoryImpl(
            franchiseApiDatasource = get(),
            franchiseCache = get(),
            jsonCacheStore = get(),
            franchiseSnapshotMapper = get(),
        )
    }
}
