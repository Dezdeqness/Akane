package com.dezdeqness.views.data.datasource

import com.dezdeqness.views.contract.model.EpisodeTimecodeEntity

interface ViewsApiDatasource {

    suspend fun getReleaseTimecodes(releaseId: Long): Result<List<EpisodeTimecodeEntity>>

    suspend fun saveTimecodes(timecodes: List<EpisodeTimecodeEntity>): Result<Unit>
}
