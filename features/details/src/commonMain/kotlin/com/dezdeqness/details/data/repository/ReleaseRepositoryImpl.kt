package com.dezdeqness.details.data.repository

import com.dezdeqness.details.data.datasource.ReleaseApiDatasource
import com.dezdeqness.details.domain.repository.ReleaseRepository

class ReleaseRepositoryImpl(
    private val releaseApiDatasource: ReleaseApiDatasource,
) : ReleaseRepository {

    override suspend fun getReleaseById(id: Int) = releaseApiDatasource.getRelease(id = id)

}
