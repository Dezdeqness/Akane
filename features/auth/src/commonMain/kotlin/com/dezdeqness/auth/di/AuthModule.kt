package com.dezdeqness.auth.di

import com.dezdeqness.auth.ui.login.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    includes(dataModule, domainModule, dataStoreModule())
    viewModelOf(::LoginViewModel)
}
