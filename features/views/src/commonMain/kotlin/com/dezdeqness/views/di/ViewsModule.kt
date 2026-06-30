package com.dezdeqness.views.di

import org.koin.dsl.module

val viewsModule = module {
    includes(timecodeDataBaseModule(), dataModule, domainModule)
}
