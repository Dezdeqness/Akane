package com.dezdeqness.release.contract.repository

import com.dezdeqness.release.contract.model.ReleaseDetailsEntity

interface ReleaseRepository {

    suspend fun getReleaseById(id: Long): Result<ReleaseDetailsEntity>

}
