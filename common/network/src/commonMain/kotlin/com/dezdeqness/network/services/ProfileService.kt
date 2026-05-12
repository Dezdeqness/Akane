package com.dezdeqness.network.services

import com.dezdeqness.network.auth.AuthHeader.REQUIRES_AUTH
import com.dezdeqness.network.constants.ApiEndPoints.PROFILE_ME
import com.dezdeqness.network.models.response.ProfileResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers

interface ProfileService {

    @Headers(REQUIRES_AUTH)
    @GET(PROFILE_ME)
    suspend fun getProfile(): Response<ProfileResponse>
}
