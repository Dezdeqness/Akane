package com.dezdeqness.franchise.data.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.cache.staleWhileRevalidate
import com.dezdeqness.foundation.cache.JsonCacheStore
import com.dezdeqness.franchise.contract.model.FranchiseEntity
import com.dezdeqness.franchise.contract.repository.FranchiseRepository
import com.dezdeqness.franchise.data.cache.FranchiseCache
import com.dezdeqness.franchise.data.cache.FranchiseSnapshot
import com.dezdeqness.franchise.data.cache.FranchiseSnapshotMapper
import com.dezdeqness.franchise.data.datasource.FranchiseApiDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.builtins.ListSerializer
import kotlin.time.Duration.Companion.hours

class FranchiseRepositoryImpl(
    private val franchiseApiDatasource: FranchiseApiDatasource,
    private val franchiseCache: FranchiseCache,
    private val jsonCacheStore: JsonCacheStore,
    private val franchiseSnapshotMapper: FranchiseSnapshotMapper,
) : FranchiseRepository {

    override suspend fun getFranchises() =
        franchiseApiDatasource.getFranchises()
            .onSuccess {
                franchiseCache.clear()
                franchiseCache.putAll(it)
            }

    override fun getRandomFranchises(limit: Int): Flow<Result<CachedResult<List<FranchiseEntity>>>> =
        staleWhileRevalidate(
            read = {
                jsonCacheStore
                    .read(RANDOM_KEY, SNAPSHOT_SERIALIZER, TTL_MILLIS)
                    ?.map(franchiseSnapshotMapper::toEntity)
            },
            fetch = { franchiseApiDatasource.getRandomFranchises(limit = limit) },
            write = { franchises ->
                jsonCacheStore.write(RANDOM_KEY, franchises.map(franchiseSnapshotMapper::toSnapshot), SNAPSHOT_SERIALIZER)
                franchiseCache.putAll(franchises)
            },
        )

    override suspend fun getFranchiseById(franchiseId: String) =
        franchiseApiDatasource.getFranchiseById(franchiseId = franchiseId)
            .onSuccess { franchiseCache.putAll(listOf(it.franchise)) }

    override suspend fun getFranchisesByRelease(releaseId: Long) =
        franchiseApiDatasource.getFranchisesByRelease(releaseId = releaseId)

    override suspend fun getCachedFranchise(franchiseId: String) =
        runCatching { franchiseCache.get(franchiseId) }

    private companion object {
        const val RANDOM_KEY = "franchise_random"
        val TTL_MILLIS = 24.hours.inWholeMilliseconds
        val SNAPSHOT_SERIALIZER = ListSerializer(FranchiseSnapshot.serializer())
    }
}
