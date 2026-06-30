package com.dezdeqness.views.data.repository

import com.dezdeqness.views.contract.model.EpisodeTimecodeEntity
import com.dezdeqness.views.contract.repository.ViewsRepository
import com.dezdeqness.views.data.db.TimecodeDao
import com.dezdeqness.views.data.mapper.TimecodeMapper

class ViewsRepositoryImpl(
    private val timecodeDao: TimecodeDao,
    private val timecodeMapper: TimecodeMapper,
) : ViewsRepository {

    override suspend fun getReleaseTimecodes(releaseId: Long): Result<List<EpisodeTimecodeEntity>> =
        runCatching {
            timecodeDao.getByRelease(releaseId).map(timecodeMapper::toEntity)
        }

    override suspend fun saveTimecode(
        releaseId: Long,
        timecode: EpisodeTimecodeEntity,
    ): Result<Unit> =
        runCatching {
            timecodeDao.upsert(timecodeMapper.toLocal(timecode, releaseId, isSynced = false))
        }
}
