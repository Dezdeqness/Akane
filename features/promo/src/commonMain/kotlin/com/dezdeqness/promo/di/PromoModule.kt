package com.dezdeqness.promo.di

import org.koin.dsl.module

val promoModule = module {
    includes(dataModule, domainModule)
}
