package com.dezdeqness.auth.di

import com.dezdeqness.auth.ui.forgot.ForgotPasswordViewModel
import com.dezdeqness.auth.ui.login.LoginViewModel
import com.dezdeqness.auth.ui.reset.ResetPasswordViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    includes(dataModule, domainModule, dataStoreModule())
    viewModelOf(::LoginViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::ResetPasswordViewModel)
}
