package com.dezdeqness.network.services

import com.dezdeqness.network.auth.AuthHeader.REQUIRES_AUTH
import com.dezdeqness.network.constants.ApiEndPoints.FAVORITES
import com.dezdeqness.network.constants.ApiEndPoints.FAVORITES_IDS
import com.dezdeqness.network.constants.ApiEndPoints.FAVORITES_RELEASES
import com.dezdeqness.network.models.core.GeneralResponse
import com.dezdeqness.network.models.request.FavoriteReleaseRequest
import com.dezdeqness.network.models.request.FavoritesReleasesRequest
import com.dezdeqness.network.models.response.ReleaseResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface FavoritesService {

    @Headers(REQUIRES_AUTH)
    @GET(FAVORITES_IDS)
    suspend fun getFavoriteIds(): Response<List<Long>>

    @Headers(REQUIRES_AUTH)
    @POST(FAVORITES)
    suspend fun addToFavorites(@Body body: List<FavoriteReleaseRequest>): Response<List<Long>>

    @Headers(REQUIRES_AUTH)
    @DELETE(FAVORITES)
    suspend fun removeFromFavorites(@Body body: List<FavoriteReleaseRequest>): Response<List<Long>>

    @Headers(REQUIRES_AUTH)
    @POST(FAVORITES_RELEASES)
    suspend fun getFavoriteReleases(
        @Body body: FavoritesReleasesRequest,
    ): Response<GeneralResponse<List<ReleaseResponse>>>
}
