package com.dezdeqness.details.data.datasource.impl

import com.dezdeqness.details.data.datasource.ReleaseApiDatasource
import com.dezdeqness.details.data.mapper.ReleaseMapper
import com.dezdeqness.network.services.CatalogService

class ReleaseApiDatasourceImpl(
    private val catalogService: CatalogService,
    private val releaseMapper: ReleaseMapper,
) : ReleaseApiDatasource {

    override suspend fun getRelease(id: Long) = tryWithCatch {
        val response = catalogService.getReleaseById(id = id)

        if (response.isSuccessful) {
            val body = response.body()
                ?: return@tryWithCatch Result.failure(Throwable("Code: ${response.code}\nError: ${response.errorBody()}"))

            val entity = releaseMapper.map(body)

            Result.success(entity)
        } else {
            // TODO: custom APIException
            Result.failure(Throwable("Code: ${response.code}\nError: ${response.errorBody()}"))
        }
    }

    private suspend fun <T> tryWithCatch(block: suspend () -> Result<T>) = try {
        block()
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }

}
