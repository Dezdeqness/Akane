package com.dezdeqness.videoplayer.ui

import com.dezdeqness.downloads.domain.model.DownloadEntity
import com.dezdeqness.videoplayer.core.player.data.MediaItem
import com.dezdeqness.videoplayer.core.player.data.MediaSource
import com.dezdeqness.videoplayer.core.player.data.QualityVariant
import com.dezdeqness.videoplayer.core.player.data.SkipRange
import com.dezdeqness.videoplayer.core.player.data.toTransformToMediaQuality
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem
import kotlin.collections.component1
import kotlin.collections.component2

class MediaItemMapper {

    fun toFilePathMediaItem(entity: DownloadEntity) = MediaItem(
        id = entity.episodeId,
        name = entity.episodeName,
        ordinal = entity.episodeOrdinal,
        source = MediaSource.FilePath(entity.filePath!!),
        previewUrl = entity.previewUrl,
        opening = entity.opening?.let { SkipRange(it.start * 1000, it.end * 1000) },
        ending = entity.ending?.let { SkipRange(it.start * 1000, it.end * 1000) },
    )

    fun toMultiQualityMediaItem(episode: EpisodeUiItem) =
        MediaItem(
            id = episode.id,
            name = episode.name,
            ordinal = episode.ordinal,
            source = MediaSource.MultiQuality(
                variants = episode.episodeUrls.map { (quality, url) ->
                    QualityVariant(
                        quality = quality.toTransformToMediaQuality(),
                        url = url,
                    )
                },
            ),
            previewUrl = episode.previewUrl,
            opening = episode.opening?.let { SkipRange(it.start * 1000, it.end * 1000) },
            ending = episode.ending?.let { SkipRange(it.start * 1000, it.end * 1000) },
        )

}
