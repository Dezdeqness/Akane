package com.dezdeqness.promo.data.repository

import com.dezdeqness.promo.contract.repository.PromoRepository
import com.dezdeqness.promo.data.datasource.PromoApiDatasource

class PromoRepositoryImpl(
    private val promoApiDatasource: PromoApiDatasource,
) : PromoRepository {

    override suspend fun getPromotions() = promoApiDatasource.getPromotions()
}
