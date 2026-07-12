package com.dezdeqness.genre.data.cache

import kotlinx.serialization.Serializable

@Serializable
data class GenreSnapshot(
    val id: Int,
    val name: String,
    val image: GenreImageSnapshot,
    val totalReleases: Int,
)

@Serializable
data class GenreImageSnapshot(
    val preview: String,
    val thumbnail: String,
    val optimizedPreview: String,
    val optimizedThumbnail: String,
)
