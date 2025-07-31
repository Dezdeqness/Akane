package com.dezdeqness.feed.data.datasource.impl

import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.data.mapper.FeedMapper
import com.dezdeqness.feed.domain.model.FeedEntity
import com.dezdeqness.network.services.CatalogService

class FeedApiDatasourceImpl(
    private val catalogService: CatalogService,
    private val feedMapper: FeedMapper,
) : FeedApiDatasource {
    override suspend fun getFeed(page: Int) = tryWithCatch {
        val response = catalogService.getReleases(
            page = page,
            limit = LIMIT,
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

    suspend fun <T> tryWithCatch(block: suspend () -> Result<T>) = try {
        block()
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }

    companion object {
        private const val LIMIT = 15
    }

}
