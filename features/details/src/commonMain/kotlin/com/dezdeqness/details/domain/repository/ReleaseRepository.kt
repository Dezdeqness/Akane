package com.dezdeqness.details.domain.repository

import com.dezdeqness.details.domain.model.ReleaseDetailsEntity

interface ReleaseRepository {

    suspend fun getReleaseById(id: Int): Result<ReleaseDetailsEntity>

}
