package com.dezdeqness.details.data.datasource.impl

import com.dezdeqness.details.data.datasource.ReleaseApiDatasource
import com.dezdeqness.details.data.mapper.ReleaseMapper
import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.services.CatalogService

class ReleaseApiDatasourceImpl(
    private val catalogService: CatalogService,
    private val releaseMapper: ReleaseMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), ReleaseApiDatasource {

    override suspend fun getRelease(id: Long) = tryWithCatchSuspend {
        val response = catalogService.getReleaseById(id = id)

        if (response.isSuccessful) {
            val body = response.body()
                ?: throw response.createApiException()

            val entity = releaseMapper.map(body)

            Result.success(entity)
        } else {
            throw response.createApiException()
        }
    }
}
