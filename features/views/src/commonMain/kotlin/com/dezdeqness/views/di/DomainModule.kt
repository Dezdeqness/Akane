package com.dezdeqness.views.di

import com.dezdeqness.views.contract.repository.ViewsRepository
import com.dezdeqness.views.data.repository.ViewsRepositoryImpl
import org.koin.dsl.module

val domainModule = module {
    single<ViewsRepository> {
        ViewsRepositoryImpl(
            timecodeDao = get(),
            timecodeMapper = get(),
        )
    }
}
