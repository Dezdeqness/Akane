package com.dezdeqness.feed.di

import org.koin.dsl.module

val videoPlayerModule = module {
    includes(domainModule)

}
