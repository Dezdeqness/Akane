package com.dezdeqness.views.data.mapper

import com.dezdeqness.network.models.request.EpisodeTimecodeRequest
import com.dezdeqness.network.models.response.EpisodeViewResponse
import com.dezdeqness.views.contract.model.EpisodeTimecodeEntity
import com.dezdeqness.views.data.db.TimecodeLocal

class TimecodeMapper {

    fun map(response: EpisodeViewResponse): EpisodeTimecodeEntity? {
        val episodeId = response.releaseEpisodeId ?: return null
        return EpisodeTimecodeEntity(
            releaseEpisodeId = episodeId,
            timeMs = ((response.time ?: 0.0) * 1_000).toLong(),
            isWatched = response.isWatched ?: false,
        )
    }

    fun map(entity: EpisodeTimecodeEntity): EpisodeTimecodeRequest =
        EpisodeTimecodeRequest(
            time = entity.timeMs / 1_000.0,
            isWatched = entity.isWatched,
            releaseEpisodeId = entity.releaseEpisodeId,
        )

    fun toLocal(
        entity: EpisodeTimecodeEntity,
        releaseId: Long,
        isSynced: Boolean,
    ): TimecodeLocal =
        TimecodeLocal(
            releaseEpisodeId = entity.releaseEpisodeId,
            releaseId = releaseId,
            timeMs = entity.timeMs,
            isWatched = entity.isWatched,
            isSynced = isSynced,
        )

    fun toEntity(local: TimecodeLocal): EpisodeTimecodeEntity =
        EpisodeTimecodeEntity(
            releaseEpisodeId = local.releaseEpisodeId,
            timeMs = local.timeMs,
            isWatched = local.isWatched,
        )
}
