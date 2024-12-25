package com.dezdeqness.feed.data.datasource.impl

import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.data.mapper.FeedMapper
import com.dezdeqness.feed.domain.model.FeedEntity
import com.dezdeqness.network.constants.ApiParams
import com.dezdeqness.network.services.FeedService

class FeedApiDatasourceImpl(
    private val feedService: FeedService,
    private val feedMapper: FeedMapper,
) : FeedApiDatasource {
    override suspend fun getFeed(page: Int) = tryWithCatch {
        val response = feedService.getFeed(ApiParams.FeedParams.createFeedParams(page = page))

        if (response.isSuccessful) {
            val items = response
                .body()
                ?.data
                .orEmpty()
                .mapNotNull(feedMapper::map)
            Result.success(
                FeedEntity(
                    items = items,
                    page = page,
                    hasNextPage = false,
                )
            )
        } else {
            // TODO: custom APIException
            Result.failure(Throwable("Code: ${response.code}\nError: ${response.errorBody()}"))
        }
    }

    suspend fun <T> tryWithCatch(block: suspend () -> Result<T>) = try {
        block()
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }

}
