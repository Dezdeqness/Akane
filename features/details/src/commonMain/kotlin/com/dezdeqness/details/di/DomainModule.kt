package com.dezdeqness.details.di

import com.dezdeqness.details.data.repository.FranchiseRepositoryImpl
import com.dezdeqness.details.data.repository.ReleaseRepositoryImpl
import com.dezdeqness.release.contract.repository.FranchiseRepository
import com.dezdeqness.release.contract.repository.ReleaseRepository
import org.koin.dsl.module

val domainModule = module {
    single<ReleaseRepository> {
        ReleaseRepositoryImpl(
            releaseApiDatasource = get(),
            jsonCacheStore = get(),
            releaseDetailsCacheMapper = get(),
        )
    }
    single<FranchiseRepository> {
        FranchiseRepositoryImpl(franchiseDatasource = get())
    }
}
