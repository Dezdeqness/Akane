package com.dezdeqness.network.services

import com.dezdeqness.network.constants.ApiEndPoints.CATALOG_RELEASES
import com.dezdeqness.network.constants.ApiEndPoints.RELEASE
import com.dezdeqness.network.models.core.GeneralResponse
import com.dezdeqness.network.models.response.ReleaseResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface CatalogService {
    @GET(CATALOG_RELEASES)
    suspend fun getReleases(
        @Query limit: Int,
        @Query page: Int,
    ): Response<GeneralResponse<List<ReleaseResponse>>>

    @GET(RELEASE)
    suspend fun getReleaseById(@Path id: Int): Response<ReleaseResponse>

}
