package com.dezdeqness.calendar.di

import com.dezdeqness.calendar.contract.repository.CalendarRepository
import com.dezdeqness.calendar.data.cache.CalendarCacheMapper
import com.dezdeqness.calendar.data.repository.CalendarRepositoryImpl
import org.koin.dsl.module

val domainModule = module {
    single { CalendarCacheMapper() }
    single<CalendarRepository> {
        CalendarRepositoryImpl(
            calendarApiDatasource = get(),
            jsonCacheStore = get(),
            calendarCacheMapper = get(),
        )
    }
}
