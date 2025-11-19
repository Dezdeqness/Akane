package com.dezdeqness.feed.di

import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.data.datasource.impl.FeedApiDatasourceImpl
import com.dezdeqness.feed.data.mapper.CatalogFilterMapper
import com.dezdeqness.feed.data.mapper.FeedMapper
import org.koin.dsl.module

val dataModule = module {
    single { CatalogFilterMapper() }
    single { FeedMapper() }
    single<FeedApiDatasource> {
        FeedApiDatasourceImpl(
            catalogService = get(),
            feedMapper = get(),
        )
    }
}
