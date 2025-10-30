package com.dezdeqness.details.data.datasource

import com.dezdeqness.details.domain.model.FranchiseEntity

interface FranchiseDatasource {
    suspend fun getReleaseFranchiseById(id: Long): Result<FranchiseEntity>

}
