package com.dezdeqness.details.di

import com.dezdeqness.details.data.repository.FranchiseRepositoryImpl
import com.dezdeqness.details.data.repository.ReleaseRepositoryImpl
import com.dezdeqness.details.domain.repository.FranchiseRepository
import com.dezdeqness.details.domain.repository.ReleaseRepository
import org.koin.dsl.module

val domainModule = module {
    single<ReleaseRepository> {
        ReleaseRepositoryImpl(releaseApiDatasource = get())
    }
    single<FranchiseRepository> {
        FranchiseRepositoryImpl(franchiseDatasource = get())
    }
}
