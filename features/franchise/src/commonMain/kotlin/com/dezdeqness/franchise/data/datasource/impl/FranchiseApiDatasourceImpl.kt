package com.dezdeqness.franchise.data.datasource.impl

import com.dezdeqness.franchise.data.datasource.FranchiseApiDatasource
import com.dezdeqness.franchise.data.mapper.FranchiseDetailsMapper
import com.dezdeqness.franchise.data.mapper.FranchiseMapper
import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.services.FranchiseService

class FranchiseApiDatasourceImpl(
    private val franchiseService: FranchiseService,
    private val franchiseMapper: FranchiseMapper,
    private val franchiseDetailsMapper: FranchiseDetailsMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), FranchiseApiDatasource {

    override suspend fun getFranchises() = tryWithCatchSuspend {
        val response = franchiseService.getFranchises()
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty().map(franchiseMapper::map))
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun getRandomFranchises(limit: Int) = tryWithCatchSuspend {
        val response = franchiseService.getRandomFranchises(limit = limit)
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty().map(franchiseMapper::map))
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun getFranchiseById(franchiseId: String) = tryWithCatchSuspend {
        val response = franchiseService.getFranchiseById(id = franchiseId)
        if (response.isSuccessful) {
            val body = response.body() ?: throw response.createApiException()
            Result.success(franchiseDetailsMapper.map(body))
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun getFranchisesByRelease(releaseId: Long) = tryWithCatchSuspend {
        val response = franchiseService.getReleaseFranchiseById(id = releaseId)
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty().map(franchiseDetailsMapper::map))
        } else {
            throw response.createApiException()
        }
    }
}
