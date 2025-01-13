package com.dezdeqness.details.data.datasource

import com.dezdeqness.details.domain.model.ReleaseEntity

interface ReleaseApiDatasource {
    suspend fun getRelease(id: Int): Result<ReleaseEntity>
}
