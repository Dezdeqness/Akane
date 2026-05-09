package com.dezdeqness.calendar.di

import com.dezdeqness.calendar.data.repository.CalendarRepositoryImpl
import com.dezdeqness.calendar.contract.repository.CalendarRepository
import org.koin.dsl.module

val domainModule = module {
    single<CalendarRepository> {
        CalendarRepositoryImpl(
            calendarApiDatasource = get(),
        )
    }
}
