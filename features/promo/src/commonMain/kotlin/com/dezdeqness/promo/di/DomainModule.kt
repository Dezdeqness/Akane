package com.dezdeqness.promo.di

import com.dezdeqness.promo.contract.repository.PromoRepository
import com.dezdeqness.promo.data.repository.PromoRepositoryImpl
import org.koin.dsl.module

val domainModule = module {
    single<PromoRepository> {
        PromoRepositoryImpl(
            promoApiDatasource = get(),
        )
    }
}
