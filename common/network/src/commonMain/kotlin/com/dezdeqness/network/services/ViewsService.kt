package com.dezdeqness.network.services

import com.dezdeqness.network.auth.AuthHeader.REQUIRES_AUTH
import com.dezdeqness.network.constants.ApiEndPoints.RELEASE_EPISODES_TIMECODES
import com.dezdeqness.network.constants.ApiEndPoints.VIEWS_TIMECODES
import com.dezdeqness.network.models.request.EpisodeTimecodeRequest
import com.dezdeqness.network.models.response.EpisodeViewResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface ViewsService {

    @Headers(REQUIRES_AUTH)
    @GET(RELEASE_EPISODES_TIMECODES)
    suspend fun getReleaseTimecodes(@Path id: Long): Response<List<EpisodeViewResponse>>

    @Headers(REQUIRES_AUTH)
    @POST(VIEWS_TIMECODES)
    suspend fun saveTimecodes(@Body body: List<EpisodeTimecodeRequest>): Response<Unit>
}
