package com.dezdeqness.details.data.datasource.impl

import com.dezdeqness.details.data.datasource.FranchiseDatasource
import com.dezdeqness.details.data.mapper.FranchiseMapper
import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.services.FranchiseService

class FranchiseDatasourceImpl(
    private val franchiseService: FranchiseService,
    private val franchiseMapper: FranchiseMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), FranchiseDatasource {

    override suspend fun getReleaseFranchiseById(id: Long) = tryWithCatchSuspend {
        val response = franchiseService.getReleaseFranchiseById(id = id)

        if (response.isSuccessful) {
            val body = response.body()
                ?: throw response.createApiException()

            val data = body.first()

            val entity = franchiseMapper.map(data)

            Result.success(entity)
        } else {
            throw response.createApiException()
        }
    }
}
