package com.dezdeqness.downloads.data.db

import androidx.room.Embedded
import androidx.room.Relation

data class DownloadWithProgress(
    @Embedded val episode: DownloadEpisodeLocal,
    @Relation(
        parentColumn = "id",
        entityColumn = "episodeDownloadId",
    )
    val progress: DownloadProgressLocal?,
)
