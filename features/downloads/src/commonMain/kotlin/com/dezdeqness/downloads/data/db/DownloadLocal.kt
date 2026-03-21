package com.dezdeqness.downloads.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "download_episodes")
data class DownloadEpisodeLocal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val releaseId: Long,
    val releaseTitle: String,
    val episodeId: String,
    val episodeName: String,
    val episodeOrdinal: Long,
    val quality: String,
    val hlsUrl: String,
    val filePath: String?,
    val createdAt: Long,
    val previewUrl: String,
)

@Entity(
    tableName = "download_progress",
    foreignKeys = [
        ForeignKey(
            entity = DownloadEpisodeLocal::class,
            parentColumns = ["id"],
            childColumns = ["episodeDownloadId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("episodeDownloadId", unique = true)],
)
data class DownloadProgressLocal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val episodeDownloadId: Long,
    val status: String,
    val progress: Float,
    val totalSegments: Int,
    val downloadedSegments: Int,
    val hiddenFromHistory: Boolean = false,
)
