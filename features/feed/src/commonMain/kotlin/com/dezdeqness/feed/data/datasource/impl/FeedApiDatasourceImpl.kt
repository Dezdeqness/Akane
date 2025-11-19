package com.dezdeqness.feed.data.datasource.impl

import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.data.mapper.FeedMapper
import com.dezdeqness.feed.domain.model.FeedEntity
import com.dezdeqness.feed.domain.model.ProductionStatus
import com.dezdeqness.feed.domain.model.PublishStatus
import com.dezdeqness.feed.domain.model.Sorting
import com.dezdeqness.network.constants.ApiParams.QUERY_PRODUCTION_STATUSES
import com.dezdeqness.network.constants.ApiParams.QUERY_PUBLISH_STATUSES
import com.dezdeqness.network.constants.ApiParams.QUERY_SORTING
import com.dezdeqness.network.services.CatalogService

class FeedApiDatasourceImpl(
    private val catalogService: CatalogService,
    private val feedMapper: FeedMapper,
) : FeedApiDatasource {
    override suspend fun getFeed(page: Int) = getFeed(page = page, limit = LIMIT, queryMap = mapOf())

    override suspend fun getFeedBestRating(limit: Int) = getFeedByQueryMap(
        limit = limit,
        queryMap = mapOf(
            QUERY_SORTING to Sorting.RATING_DESC.name,
        ),
    )

    override suspend fun getFeedOngoing(limit: Int) = getFeedByQueryMap(
        limit = limit,
        queryMap = mapOf(
            QUERY_PUBLISH_STATUSES to PublishStatus.IS_ONGOING.name,
        ),
    )

    override suspend fun getFeedReleased(limit: Int) = getFeedByQueryMap(
        limit = limit,
        queryMap = mapOf(
            QUERY_PRODUCTION_STATUSES to ProductionStatus.IS_NOT_IN_PRODUCTION.name,
        ),
    )

    override suspend fun getFeed(
        page: Int,
        limit: Int,
        queryMap: Map<String, Any>,
    ) = tryWithCatch {
        val response = catalogService.getReleases(
            page = page,
            limit = limit,
            queryMap = queryMap,
        )

        if (response.isSuccessful) {
            val body = response.body()

            val items = body
                ?.data
                .orEmpty()
                .map(feedMapper::map)

            val nextPage = (body?.meta?.pagination?.currentPage ?: 0) + 1
            val currentPage = (body?.meta?.pagination?.currentPage ?: 0)
            val totalPages = body?.meta?.pagination?.totalPages ?: 0
            Result.success(
                FeedEntity(
                    items = items,
                    nextPage = nextPage,
                    currentPage = currentPage,
                    hasNextPage = nextPage < totalPages,
                )
            )
        } else {
            // TODO: custom APIException
            Result.failure(Throwable("Code: ${response.code}\nError: ${response.errorBody()}"))
        }
    }

    private suspend fun getFeedByQueryMap(
        queryMap: Map<String, Any>,
        limit: Int,
    ) = tryWithCatch {
        val response = catalogService.getReleases(
            limit = limit,
            queryMap = queryMap,
        )

        if (response.isSuccessful) {
            val body = response.body()

            val items = body
                ?.data
                .orEmpty()
                .map(feedMapper::map)

            Result.success(items)
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

    companion object {
        private const val LIMIT = 15
    }
}
