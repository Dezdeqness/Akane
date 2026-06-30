package com.dezdeqness.views.di

import com.dezdeqness.views.data.datasource.ViewsApiDatasource
import com.dezdeqness.views.data.datasource.impl.ViewsApiDatasourceImpl
import com.dezdeqness.views.data.db.TimecodeDatabase
import com.dezdeqness.views.data.mapper.TimecodeMapper
import org.koin.dsl.module

val dataModule = module {
    single { TimecodeMapper() }
    single { get<TimecodeDatabase>().timecodeDao() }
    single<ViewsApiDatasource> {
        ViewsApiDatasourceImpl(
            viewsService = get(),
            timecodeMapper = get(),
            errorMapper = get(),
        )
    }
}
