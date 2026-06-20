package com.dezdeqness.promo.data.datasource

import com.dezdeqness.promo.contract.model.PromoEntity

interface PromoApiDatasource {
    suspend fun getPromotions(): Result<List<PromoEntity>>
}
