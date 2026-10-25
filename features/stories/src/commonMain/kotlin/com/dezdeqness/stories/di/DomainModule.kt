package com.dezdeqness.stories.di

import com.dezdeqness.stories.contract.repository.StoriesRepository
import com.dezdeqness.stories.data.cache.StoryCacheMapper
import com.dezdeqness.stories.data.repository.StoriesRepositoryImpl
import org.koin.dsl.module

val domainModule = module {
    single { StoryCacheMapper() }
    single<StoriesRepository> {
        StoriesRepositoryImpl(
            storiesApiDatasource = get(),
            storyCacheMapper = get(),
            jsonCacheStore = get(),
        )
    }
}
