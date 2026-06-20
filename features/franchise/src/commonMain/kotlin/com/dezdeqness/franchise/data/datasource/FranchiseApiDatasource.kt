package com.dezdeqness.franchise.data.datasource

import com.dezdeqness.franchise.contract.model.FranchiseDetailsEntity
import com.dezdeqness.franchise.contract.model.FranchiseEntity

interface FranchiseApiDatasource {
    suspend fun getFranchises(): Result<List<FranchiseEntity>>

    suspend fun getRandomFranchises(limit: Int): Result<List<FranchiseEntity>>

    suspend fun getFranchiseById(franchiseId: String): Result<FranchiseDetailsEntity>

    suspend fun getFranchisesByRelease(releaseId: Long): Result<List<FranchiseDetailsEntity>>
}
