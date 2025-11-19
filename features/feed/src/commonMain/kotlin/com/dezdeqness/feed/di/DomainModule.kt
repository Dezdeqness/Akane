package com.dezdeqness.feed.di

import com.dezdeqness.feed.data.repository.FeedRepositoryImpl
import com.dezdeqness.feed.domain.repository.FeedRepository
import org.koin.dsl.module

val domainModule = module {
    single<FeedRepository> {
        FeedRepositoryImpl(
            feedApiDatasource = get(),
            catalogFilterMapper = get(),
        )
    }
}
