package com.dezdeqness.details.data.datasource

import com.dezdeqness.release.contract.model.FranchiseEntity

interface FranchiseDatasource {
    suspend fun getReleaseFranchiseById(id: Long): Result<FranchiseEntity>

}
