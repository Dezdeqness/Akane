package com.dezdeqness.details.data.datasource

import com.dezdeqness.details.domain.model.ReleaseDetailsEntity

interface ReleaseApiDatasource {
    suspend fun getRelease(id: Long): Result<ReleaseDetailsEntity>
}
