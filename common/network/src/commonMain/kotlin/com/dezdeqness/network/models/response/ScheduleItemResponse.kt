package com.dezdeqness.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleItemResponse(
    val release: ReleaseResponse,
    @SerialName("full_season_is_released")
    val fullSeasonIsReleased: Boolean,
    @SerialName("published_release_episode")
    val publishedReleaseEpisode: PublishedEpisode?,
    @SerialName("next_release_episode_number")
    val nextReleaseEpisodeNumber: Int?,
)
