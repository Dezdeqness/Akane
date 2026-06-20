package com.dezdeqness.feed.di

import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.data.datasource.impl.FeedApiDatasourceImpl
import com.dezdeqness.feed.data.mapper.CatalogFilterMapper
import com.dezdeqness.feed.data.mapper.FeedMapper
import com.dezdeqness.feed.data.provider.GenreProvider
import org.koin.dsl.module

val dataModule = module {
    single { GenreProvider() }
    single { CatalogFilterMapper() }
    single { FeedMapper(imageUrlBuilder = get()) }
    single<FeedApiDatasource> {
        FeedApiDatasourceImpl(
            catalogService = get(),
            feedMapper = get(),
            errorMapper = get(),
        )
    }
}
