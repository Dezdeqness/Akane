package com.dezdeqness.stories.di

import com.dezdeqness.stories.data.datasource.StoriesApiDatasource
import com.dezdeqness.stories.data.datasource.impl.StoriesApiDatasourceImpl
import com.dezdeqness.stories.data.mapper.StoriesMapper
import org.koin.dsl.module

val dataModule = module {
    single { StoriesMapper(imageUrlBuilder = get()) }
    single<StoriesApiDatasource> {
        StoriesApiDatasourceImpl(
            calendarService = get(),
            storiesMapper = get(),
            errorMapper = get(),
        )
    }
}
