package com.dezdeqness.franchise.contract.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.franchise.contract.model.FranchiseDetailsEntity
import com.dezdeqness.franchise.contract.model.FranchiseEntity
import kotlinx.coroutines.flow.Flow

interface FranchiseRepository {
    suspend fun getFranchises(): Result<List<FranchiseEntity>>

    fun getRandomFranchises(limit: Int = 10): Flow<Result<CachedResult<List<FranchiseEntity>>>>

    suspend fun getFranchiseById(franchiseId: String): Result<FranchiseDetailsEntity>

    suspend fun getFranchisesByRelease(releaseId: Long): Result<List<FranchiseDetailsEntity>>

    suspend fun getCachedFranchise(franchiseId: String): Result<FranchiseEntity?>
}
