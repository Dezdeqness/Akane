package com.dezdeqness.release.contract.repository

import com.dezdeqness.release.contract.model.FranchiseEntity

interface FranchiseRepository {

    suspend fun getReleaseFranchiseById(id: Long): Result<FranchiseEntity>

}
