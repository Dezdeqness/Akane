package com.dezdeqness.views.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode_timecode")
data class TimecodeLocal(
    @PrimaryKey
    val releaseEpisodeId: String,
    val releaseId: Long,
    val timeMs: Long,
    val isWatched: Boolean,
    val isSynced: Boolean,
)
