package com.dezdeqness.feed.data.datasource.impl

import com.dezdeqness.catalog.contract.model.ReleasesPageEntity
import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.data.mapper.FeedMapper
import com.dezdeqness.feed.contract.model.ProductionStatus
import com.dezdeqness.feed.contract.model.PublishStatus
import com.dezdeqness.feed.contract.model.Sorting
import com.dezdeqness.network.constants.ApiParams.QUERY_PRODUCTION_STATUSES
import com.dezdeqness.network.constants.ApiParams.QUERY_PUBLISH_STATUSES
import com.dezdeqness.network.constants.ApiParams.QUERY_SORTING
import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.services.CatalogService

class FeedApiDatasourceImpl(
    private val catalogService: CatalogService,
    private val feedMapper: FeedMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), FeedApiDatasource {
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
    ) = tryWithCatchSuspend {
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
                ReleasesPageEntity(
                    items = items,
                    nextPage = nextPage,
                    currentPage = currentPage,
                    hasNextPage = nextPage < totalPages,
                )
            )
        } else {
            throw response.createApiException()
        }
    }

    private suspend fun getFeedByQueryMap(
        queryMap: Map<String, Any>,
        limit: Int,
    ) = tryWithCatchSuspend {
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
            throw response.createApiException()
        }
    }

    companion object {
        private const val LIMIT = 15
    }
}
