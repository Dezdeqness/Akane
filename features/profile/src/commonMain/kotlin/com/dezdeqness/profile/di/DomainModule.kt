package com.dezdeqness.profile.di

import com.dezdeqness.profile.contract.repository.ProfileRepository
import com.dezdeqness.profile.data.repository.ProfileRepositoryImpl
import org.koin.dsl.module

val domainModule = module {
    single<ProfileRepository> {
        ProfileRepositoryImpl(
            profileApiDatasource = get(),
        )
    }
}
