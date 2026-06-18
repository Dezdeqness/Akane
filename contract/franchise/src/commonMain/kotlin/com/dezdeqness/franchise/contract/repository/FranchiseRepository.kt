package com.dezdeqness.franchise.contract.repository

import com.dezdeqness.franchise.contract.model.FranchiseDetailsEntity
import com.dezdeqness.franchise.contract.model.FranchiseEntity

interface FranchiseRepository {
    suspend fun getFranchises(): Result<List<FranchiseEntity>>

    suspend fun getRandomFranchises(limit: Int = 10): Result<List<FranchiseEntity>>

    suspend fun getFranchiseById(franchiseId: String): Result<FranchiseDetailsEntity>

    suspend fun getFranchisesByRelease(releaseId: Long): Result<List<FranchiseDetailsEntity>>
}
