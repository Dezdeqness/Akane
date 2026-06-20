package com.dezdeqness.promo.di

import com.dezdeqness.promo.data.datasource.PromoApiDatasource
import com.dezdeqness.promo.data.datasource.impl.PromoApiDatasourceImpl
import com.dezdeqness.promo.data.mapper.PromoMapper
import org.koin.dsl.module

val dataModule = module {
    single { PromoMapper(imageUrlBuilder = get()) }
    single<PromoApiDatasource> {
        PromoApiDatasourceImpl(
            promotionsService = get(),
            promoMapper = get(),
            errorMapper = get(),
        )
    }
}
