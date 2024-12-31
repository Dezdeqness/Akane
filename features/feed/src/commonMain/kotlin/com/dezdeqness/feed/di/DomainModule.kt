package com.dezdeqness.feed.di

import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.data.datasource.impl.FeedApiDatasourceImpl
import com.dezdeqness.feed.data.mapper.FeedMapper
import com.dezdeqness.feed.data.repository.FeedRepositoryImpl
import com.dezdeqness.feed.domain.repository.FeedRepository
import com.dezdeqness.network.di.networkModule
import org.koin.dsl.module

val domainModule = module {
    includes(networkModule)
    single { FeedMapper() }
    single<FeedApiDatasource> {
        FeedApiDatasourceImpl(
            catalogService = get(),
            feedMapper = get(),
        )
    }
    single<FeedRepository> {
        FeedRepositoryImpl(feedApiDatasource = get())
    }
}
