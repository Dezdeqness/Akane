package com.dezdeqness.network.services

import com.dezdeqness.network.constants.ApiEndPoints.PROMOTIONS
import com.dezdeqness.network.models.response.PromotionResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET

interface PromotionsService {
    @GET(PROMOTIONS)
    suspend fun getPromotions(): Response<List<PromotionResponse>>
}
