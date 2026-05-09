package com.dezdeqness.details.data.repository

import com.dezdeqness.details.data.datasource.ReleaseApiDatasource
import com.dezdeqness.release.contract.repository.ReleaseRepository

class ReleaseRepositoryImpl(
    private val releaseApiDatasource: ReleaseApiDatasource,
) : ReleaseRepository {

    override suspend fun getReleaseById(id: Long) = releaseApiDatasource.getRelease(id = id)

}
