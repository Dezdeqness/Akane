package com.dezdeqness.details.domain.repository

import com.dezdeqness.details.domain.model.FranchiseEntity

interface FranchiseRepository {

    suspend fun getReleaseFranchiseById(id: Long): Result<FranchiseEntity>

}
