package com.dezdeqness.views.contract.repository

import com.dezdeqness.views.contract.model.EpisodeTimecodeEntity
import kotlinx.coroutines.flow.Flow

interface ViewsRepository {

    suspend fun getReleaseTimecodes(releaseId: Long): Result<List<EpisodeTimecodeEntity>>

    suspend fun getTimecodesByRecency(): Result<List<EpisodeTimecodeEntity>>

    fun getTimecodesByRecencyAsFlow(): Flow<List<EpisodeTimecodeEntity>>

    fun getReleaseTimecodesAsFlow(releaseId: Long): Flow<List<EpisodeTimecodeEntity>>

    suspend fun saveTimecode(releaseId: Long, timecode: EpisodeTimecodeEntity): Result<Unit>
}
