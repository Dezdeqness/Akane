package com.dezdeqness.core.di

import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProviderImpl
import org.koin.dsl.module

val coreModule = module {
    single<CoroutineDispatcherProvider> { CoroutineDispatcherProviderImpl() }
}
