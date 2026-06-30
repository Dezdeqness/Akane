package com.dezdeqness.views.contract.repository

import com.dezdeqness.views.contract.model.EpisodeTimecodeEntity

interface ViewsRepository {

    suspend fun getReleaseTimecodes(releaseId: Long): Result<List<EpisodeTimecodeEntity>>

    suspend fun saveTimecode(releaseId: Long, timecode: EpisodeTimecodeEntity): Result<Unit>
}
