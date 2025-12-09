package com.dezdeqness.calendar.di

import com.dezdeqness.calendar.data.datasource.CalendarApiDatasource
import com.dezdeqness.calendar.data.datasource.impl.CalendarApiDatasourceImpl
import com.dezdeqness.calendar.data.mapper.CalendarMapper
import org.koin.dsl.module

val dataModule = module {
    single { CalendarMapper() }
    single<CalendarApiDatasource> {
        CalendarApiDatasourceImpl(
            calendarService = get(),
            calendarMapper = get(),
        )
    }
}
