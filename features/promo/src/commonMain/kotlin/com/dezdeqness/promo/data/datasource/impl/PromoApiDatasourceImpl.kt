package com.dezdeqness.promo.data.datasource.impl

import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.services.PromotionsService
import com.dezdeqness.promo.data.datasource.PromoApiDatasource
import com.dezdeqness.promo.data.mapper.PromoMapper

class PromoApiDatasourceImpl(
    private val promotionsService: PromotionsService,
    private val promoMapper: PromoMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), PromoApiDatasource {

    override suspend fun getPromotions() = tryWithCatchSuspend {
        val response = promotionsService.getPromotions()
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty().map(promoMapper::map))
        } else {
            throw response.createApiException()
        }
    }
}
