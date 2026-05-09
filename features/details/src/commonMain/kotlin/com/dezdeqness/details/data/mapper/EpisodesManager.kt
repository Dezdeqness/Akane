package com.dezdeqness.details.data.mapper

import com.dezdeqness.release.contract.model.EpisodeEntity
import com.dezdeqness.release.contract.model.TimingEntity
import com.dezdeqness.release.contract.model.VideoQuality
import com.dezdeqness.network.models.core.Episode
import com.dezdeqness.network.models.core.Timing

class EpisodesManager {

    fun map(response: Episode) =
        EpisodeEntity(
            id = response.id,
            name = response.name.orEmpty(),
            previewUrl = response.preview.src.orEmpty(),
            episodeUrls = mapEpisodeUrls(response),
            ordinal = response.ordinal,
            duration = response.duration,
            updatedAt = response.updatedAt,
            nameEnglish = response.nameEnglish.orEmpty(),
            opening = mapTiming(response.opening),
            ending = mapTiming(response.ending),
        )

    private fun mapTiming(timing: Timing): TimingEntity? {
        val start = timing.start
        val stop = timing.stop

        if (start == null || stop == null) {
            return null
        }

        return TimingEntity(
            start = start,
            end = stop,
        )
    }

    private fun mapEpisodeUrls(response: Episode): LinkedHashMap<VideoQuality, String> {
        val map = LinkedHashMap<VideoQuality, String>()
        response.hls480?.takeIf { it.isNotBlank() }?.let {
            map.put(VideoQuality.q480, it)
        }
        response.hls720?.takeIf { it.isNotBlank() }?.let {
            map.put(VideoQuality.q720, it)
        }
        response.hls1080?.takeIf { it.isNotBlank() }?.let {
            map.put(VideoQuality.q1080, it)
        }
        return map
    }

}
