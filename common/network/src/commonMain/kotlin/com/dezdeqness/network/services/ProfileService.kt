package com.dezdeqness.network.services

import com.dezdeqness.network.constants.ApiEndPoints.PROFILE_ME
import com.dezdeqness.network.models.response.ProfileResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET

interface ProfileService {

    @GET(PROFILE_ME)
    suspend fun getProfile(): Response<ProfileResponse>
}
