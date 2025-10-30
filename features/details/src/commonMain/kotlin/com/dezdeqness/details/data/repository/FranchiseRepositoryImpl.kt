package com.dezdeqness.details.data.repository

import com.dezdeqness.details.data.datasource.FranchiseDatasource
import com.dezdeqness.details.domain.model.FranchiseEntity
import com.dezdeqness.details.domain.repository.FranchiseRepository

class FranchiseRepositoryImpl(
    private val franchiseDatasource: FranchiseDatasource,
) : FranchiseRepository {

    override suspend fun getReleaseFranchiseById(id: Long): Result<FranchiseEntity> =
        franchiseDatasource.getReleaseFranchiseById(id = id)

}