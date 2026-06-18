package com.dezdeqness.promo.contract.repository

import com.dezdeqness.promo.contract.model.PromoEntity

interface PromoRepository {
    suspend fun getPromotions(): Result<List<PromoEntity>>
}
