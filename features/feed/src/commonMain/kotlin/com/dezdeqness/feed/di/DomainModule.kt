package com.dezdeqness.feed.di

import com.dezdeqness.feed.data.cache.FeedCacheMapper
import com.dezdeqness.feed.data.repository.FeedRepositoryImpl
import com.dezdeqness.feed.contract.repository.FeedRepository
import org.koin.dsl.module

val domainModule = module {
    single { FeedCacheMapper() }
    single<FeedRepository> {
        FeedRepositoryImpl(
            feedApiDatasource = get(),
            catalogFilterMapper = get(),
            jsonCacheStore = get(),
            feedCacheMapper = get(),
        )
    }
}
