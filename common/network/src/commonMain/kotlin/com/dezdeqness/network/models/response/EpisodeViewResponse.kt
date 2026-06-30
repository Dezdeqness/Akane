package com.dezdeqness.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeViewResponse(
    val id: Long? = null,
    @SerialName("time")
    val time: Double? = null,
    @SerialName("user_id")
    val userId: Long? = null,
    @SerialName("is_watched")
    val isWatched: Boolean? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("release_episode_id")
    val releaseEpisodeId: String? = null,
)
