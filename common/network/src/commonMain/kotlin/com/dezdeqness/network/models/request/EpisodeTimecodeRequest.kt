package com.dezdeqness.network.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeTimecodeRequest(
    val time: Double,
    @SerialName("is_watched")
    val isWatched: Boolean,
    @SerialName("release_episode_id")
    val releaseEpisodeId: String,
)
