package com.dezdeqness.details.data.cache

import com.dezdeqness.release.contract.model.EpisodeEntity
import com.dezdeqness.release.contract.model.ReleaseDetailsEntity
import com.dezdeqness.release.contract.model.TimingEntity
import com.dezdeqness.release.contract.model.VideoQuality

class ReleaseDetailsCacheMapper {

    fun toSnapshot(entity: ReleaseDetailsEntity) = ReleaseDetailsSnapshot(
        id = entity.id,
        name = entity.name,
        poster = entity.poster,
        type = entity.type,
        season = entity.season,
        description = entity.description,
        episodesTotal = entity.episodesTotal,
        genres = entity.genres,
        episodes = entity.episodes.map(::toSnapshot),
        year = entity.year,
        isOngoing = entity.isOngoing,
        ageRating = entity.ageRating,
        userFavourites = entity.userFavourites,
        averageDuration = entity.averageDuration,
        planned = entity.planned,
        watched = entity.watched,
        watching = entity.watching,
        postponed = entity.postponed,
        abandoned = entity.abandoned,
    )

    fun toEntity(snapshot: ReleaseDetailsSnapshot) = ReleaseDetailsEntity(
        id = snapshot.id,
        name = snapshot.name,
        poster = snapshot.poster,
        type = snapshot.type,
        season = snapshot.season,
        description = snapshot.description,
        episodesTotal = snapshot.episodesTotal,
        genres = snapshot.genres,
        episodes = snapshot.episodes.map(::toEntity),
        year = snapshot.year,
        isOngoing = snapshot.isOngoing,
        ageRating = snapshot.ageRating,
        userFavourites = snapshot.userFavourites,
        averageDuration = snapshot.averageDuration,
        planned = snapshot.planned,
        watched = snapshot.watched,
        watching = snapshot.watching,
        postponed = snapshot.postponed,
        abandoned = snapshot.abandoned,
    )

    private fun toSnapshot(episode: EpisodeEntity) = EpisodeSnapshot(
        id = episode.id,
        name = episode.name,
        previewUrl = episode.previewUrl,
        ordinal = episode.ordinal,
        episodeUrls = episode.episodeUrls.entries.associate { (quality, url) -> quality.name to url },
        duration = episode.duration,
        updatedAt = episode.updatedAt,
        nameEnglish = episode.nameEnglish,
        opening = episode.opening?.let { TimingSnapshot(it.start, it.end) },
        ending = episode.ending?.let { TimingSnapshot(it.start, it.end) },
    )

    private fun toEntity(snapshot: EpisodeSnapshot): EpisodeEntity {
        val urls = LinkedHashMap<VideoQuality, String>()
        snapshot.episodeUrls.forEach { (qualityName, url) ->
            val quality = runCatching { VideoQuality.valueOf(qualityName) }.getOrNull()
            if (quality != null) {
                urls[quality] = url
            }
        }
        return EpisodeEntity(
            id = snapshot.id,
            name = snapshot.name,
            previewUrl = snapshot.previewUrl,
            ordinal = snapshot.ordinal,
            episodeUrls = urls,
            duration = snapshot.duration,
            updatedAt = snapshot.updatedAt,
            nameEnglish = snapshot.nameEnglish,
            opening = snapshot.opening?.let { TimingEntity(it.start, it.end) },
            ending = snapshot.ending?.let { TimingEntity(it.start, it.end) },
        )
    }
}
