package com.dezdeqness.videoplayer.di

import org.koin.dsl.module

val videoPlayerModule = module {
    includes(domainModule)

}
