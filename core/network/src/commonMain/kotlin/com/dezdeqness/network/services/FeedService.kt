package com.dezdeqness.network.services

import com.dezdeqness.network.constants.ApiEndPoints.INDEX
import com.dezdeqness.network.models.core.GeneralResponse
import com.dezdeqness.network.models.response.FeedResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.POST

interface FeedService {
    @FormUrlEncoded
    @POST(INDEX)
    suspend fun getFeed(@FieldMap map: Map<String, Any>): Response<GeneralResponse<List<FeedResponse>>>

}
