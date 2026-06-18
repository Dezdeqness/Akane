package com.dezdeqness.network.services

import com.dezdeqness.network.constants.ApiEndPoints.FRANCHISE
import com.dezdeqness.network.constants.ApiEndPoints.FRANCHISES
import com.dezdeqness.network.constants.ApiEndPoints.FRANCHISES_RANDOM
import com.dezdeqness.network.constants.ApiEndPoints.FRANCHISE_BY_ID
import com.dezdeqness.network.models.response.FranchiseDetailResponse
import com.dezdeqness.network.models.response.FranchiseItemResponse
import com.dezdeqness.network.models.response.FranchiseResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface FranchiseService {
    @GET(FRANCHISE)
    suspend fun getReleaseFranchiseById(@Path id: Long): Response<List<FranchiseResponse>>

    @GET(FRANCHISES)
    suspend fun getFranchises(): Response<List<FranchiseItemResponse>>

    @GET(FRANCHISES_RANDOM)
    suspend fun getRandomFranchises(@Query limit: Int): Response<List<FranchiseItemResponse>>

    @GET(FRANCHISE_BY_ID)
    suspend fun getFranchiseById(@Path id: String): Response<FranchiseDetailResponse>
}
