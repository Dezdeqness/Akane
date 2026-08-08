package com.dezdeqness.views.data.repository

import com.dezdeqness.views.contract.model.EpisodeTimecodeEntity
import com.dezdeqness.views.contract.repository.ViewsRepository
import com.dezdeqness.views.data.db.TimecodeDao
import com.dezdeqness.views.data.mapper.TimecodeMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ViewsRepositoryImpl(
    private val timecodeDao: TimecodeDao,
    private val timecodeMapper: TimecodeMapper,
) : ViewsRepository {

    override suspend fun getReleaseTimecodes(releaseId: Long): Result<List<EpisodeTimecodeEntity>> =
        runCatching {
            timecodeDao.getByRelease(releaseId).map(timecodeMapper::toEntity)
        }

    override suspend fun getTimecodesByRecency(): Result<List<EpisodeTimecodeEntity>> =
        runCatching {
            timecodeDao.getByRecency().map(timecodeMapper::toEntity)
        }

    override fun getReleaseTimecodesAsFlow(releaseId: Long): Flow<List<EpisodeTimecodeEntity>> =
        timecodeDao.getByReleaseAsFlow(releaseId)
            .map { locals -> locals.map(timecodeMapper::toEntity) }

    override suspend fun saveTimecode(
        releaseId: Long,
        timecode: EpisodeTimecodeEntity,
    ): Result<Unit> =
        runCatching {
            timecodeDao.upsert(timecodeMapper.toLocal(timecode, releaseId, isSynced = false))
        }
}
