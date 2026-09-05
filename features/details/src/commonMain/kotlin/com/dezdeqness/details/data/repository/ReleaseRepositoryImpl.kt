package com.dezdeqness.details.data.repository

import com.dezdeqness.details.data.cache.ReleaseDetailsCacheMapper
import com.dezdeqness.details.data.cache.ReleaseDetailsSnapshot
import com.dezdeqness.details.data.datasource.ReleaseApiDatasource
import com.dezdeqness.foundation.cache.JsonCacheStore
import com.dezdeqness.release.contract.model.ReleaseDetailsEntity
import com.dezdeqness.release.contract.repository.ReleaseRepository

class ReleaseRepositoryImpl(
    private val releaseApiDatasource: ReleaseApiDatasource,
    private val jsonCacheStore: JsonCacheStore,
    private val releaseDetailsCacheMapper: ReleaseDetailsCacheMapper,
) : ReleaseRepository {

    override suspend fun getReleaseById(id: Long): Result<ReleaseDetailsEntity> {
        val result = releaseApiDatasource.getRelease(id = id)

        result.onSuccess { details ->
            jsonCacheStore.write(
                keyFor(id),
                releaseDetailsCacheMapper.toSnapshot(details),
                SNAPSHOT_SERIALIZER,
            )
        }

        return result
    }

    override suspend fun getCachedReleaseById(id: Long): ReleaseDetailsEntity? {
        val snapshot = jsonCacheStore.read(keyFor(id), SNAPSHOT_SERIALIZER) ?: return null
        return releaseDetailsCacheMapper.toEntity(snapshot)
    }

    private fun keyFor(id: Long) = "$KEY_PREFIX$id"

    private companion object {
        const val KEY_PREFIX = "release_details_"
        val SNAPSHOT_SERIALIZER = ReleaseDetailsSnapshot.serializer()
    }
}
