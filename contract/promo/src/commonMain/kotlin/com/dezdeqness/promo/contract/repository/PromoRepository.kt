package com.dezdeqness.promo.contract.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.promo.contract.model.PromoEntity
import kotlinx.coroutines.flow.Flow

interface PromoRepository {
    fun getPromotions(): Flow<Result<CachedResult<List<PromoEntity>>>>
}
