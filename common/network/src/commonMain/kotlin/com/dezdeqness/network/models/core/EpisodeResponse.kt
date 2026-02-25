package com.dezdeqness.network.models.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: make map of hls
@Serializable
data class Episode(
    val id: String,
    val name: String?,
    val ordinal: Long,
    val opening: Timing,
    val ending: Timing,
    val preview: Preview,
    @SerialName("hls_480")
    val hls480: String?,
    @SerialName("hls_720")
    val hls720: String?,
    @SerialName("hls_1080")
    val hls1080: String?,
    val duration: Long,
    @SerialName("rutube_id")
    val rutubeId: String?,
    @SerialName("youtube_id")
    val youtubeId: String?,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("sort_order")
    val sortOrder: Long,
    @SerialName("name_english")
    val nameEnglish: String?,
)

@Serializable
data class Timing(
    val stop: Long?,
    val start: Long?,
)

@Serializable
data class Preview(
    val src: String?,
    val thumbnail: String?,
    val optimized: Optimized,
)
