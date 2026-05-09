package com.dezdeqness.details.data.datasource

import com.dezdeqness.release.contract.model.ReleaseDetailsEntity

interface ReleaseApiDatasource {
    suspend fun getRelease(id: Long): Result<ReleaseDetailsEntity>
}
