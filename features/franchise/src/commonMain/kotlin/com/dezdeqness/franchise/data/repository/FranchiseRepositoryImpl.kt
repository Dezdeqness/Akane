package com.dezdeqness.franchise.data.repository

import com.dezdeqness.franchise.contract.repository.FranchiseRepository
import com.dezdeqness.franchise.data.cache.FranchiseCache
import com.dezdeqness.franchise.data.datasource.FranchiseApiDatasource

class FranchiseRepositoryImpl(
    private val franchiseApiDatasource: FranchiseApiDatasource,
    private val franchiseCache: FranchiseCache,
) : FranchiseRepository {

    override suspend fun getFranchises() =
        franchiseApiDatasource.getFranchises()
            .onSuccess {
                franchiseCache.clear()
                franchiseCache.putAll(it)
            }

    override suspend fun getRandomFranchises(limit: Int) =
        franchiseApiDatasource.getRandomFranchises(limit = limit)
            .onSuccess { franchiseCache.putAll(it) }

    override suspend fun getFranchiseById(franchiseId: String) =
        franchiseApiDatasource.getFranchiseById(franchiseId = franchiseId)
            .onSuccess { franchiseCache.putAll(listOf(it.franchise)) }

    override suspend fun getFranchisesByRelease(releaseId: Long) =
        franchiseApiDatasource.getFranchisesByRelease(releaseId = releaseId)

    override suspend fun getCachedFranchise(franchiseId: String) =
        runCatching { franchiseCache.get(franchiseId) }
}
