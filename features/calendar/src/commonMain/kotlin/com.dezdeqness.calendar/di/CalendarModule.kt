package com.dezdeqness.calendar.di

import org.koin.dsl.module

val calendarModule = module {
    includes(dataModule, domainModule)
}
