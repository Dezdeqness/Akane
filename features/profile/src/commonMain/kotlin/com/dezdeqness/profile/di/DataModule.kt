package com.dezdeqness.profile.di

import com.dezdeqness.profile.data.datasource.ProfileApiDatasource
import com.dezdeqness.profile.data.datasource.impl.ProfileApiDatasourceImpl
import com.dezdeqness.profile.data.mapper.ProfileMapper
import org.koin.dsl.module

internal val dataModule = module {
    single { ProfileMapper() }
    single<ProfileApiDatasource> {
        ProfileApiDatasourceImpl(
            profileService = get(),
            profileMapper = get(),
            errorMapper = get(),
        )
    }
}
