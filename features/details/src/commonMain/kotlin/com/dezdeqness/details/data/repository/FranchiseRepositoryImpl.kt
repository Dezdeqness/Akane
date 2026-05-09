package com.dezdeqness.details.data.repository

import com.dezdeqness.details.data.datasource.FranchiseDatasource
import com.dezdeqness.release.contract.model.FranchiseEntity
import com.dezdeqness.release.contract.repository.FranchiseRepository

class FranchiseRepositoryImpl(
    private val franchiseDatasource: FranchiseDatasource,
) : FranchiseRepository {

    override suspend fun getReleaseFranchiseById(id: Long): Result<FranchiseEntity> =
        franchiseDatasource.getReleaseFranchiseById(id = id)

}