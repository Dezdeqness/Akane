package com.dezdeqness.network.models.response

import com.dezdeqness.network.models.core.Ending
import com.dezdeqness.network.models.core.Opening
import com.dezdeqness.network.models.core.Preview
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublishedEpisode(
    val id: String,
    val name: String?,
    val ordinal: Double,
    val opening: Opening,
    val ending: Ending,
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
    @SerialName("release_id")
    val releaseId: Long,
    @SerialName("name_english")
    val nameEnglish: String?,
)
