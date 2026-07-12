package com.dezdeqness.promo.di

import com.dezdeqness.promo.contract.repository.PromoRepository
import com.dezdeqness.promo.data.cache.PromoCacheMapper
import com.dezdeqness.promo.data.repository.PromoRepositoryImpl
import org.koin.dsl.module

val domainModule = module {
    single { PromoCacheMapper() }
    single<PromoRepository> {
        PromoRepositoryImpl(
            promoApiDatasource = get(),
            jsonCacheStore = get(),
            promoCacheMapper = get(),
        )
    }
}
