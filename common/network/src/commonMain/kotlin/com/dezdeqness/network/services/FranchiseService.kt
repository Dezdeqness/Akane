package com.dezdeqness.network.services

import com.dezdeqness.network.constants.ApiEndPoints.FRANCHISE
import com.dezdeqness.network.models.response.FranchiseResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface FranchiseService {
    @GET(FRANCHISE)
    suspend fun getReleaseFranchiseById(@Path id: Long): Response<FranchiseResponse>
}
