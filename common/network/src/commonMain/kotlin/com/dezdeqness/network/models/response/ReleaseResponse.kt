package com.dezdeqness.network.models.response

import com.dezdeqness.network.models.core.Episode
import com.dezdeqness.network.models.core.Genre
import com.dezdeqness.network.models.core.Poster
import com.dezdeqness.network.models.core.release.AgeRating
import com.dezdeqness.network.models.core.release.Name
import com.dezdeqness.network.models.core.release.PublishDay
import com.dezdeqness.network.models.core.release.Season
import com.dezdeqness.network.models.core.release.Type
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseResponse(
    val id: Long,
    val type: Type,
    val year: Long,
    val name: Name,
    val alias: String,
    val season: Season,
    val poster: Poster,
    @SerialName("fresh_at")
    val freshAt: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("is_ongoing")
    val isOngoing: Boolean,
    @SerialName("age_rating")
    val ageRating: AgeRating,
    @SerialName("publish_day")
    val publishDay: PublishDay,
    val description: String?,
    @SerialName("episodes_total")
    val episodesTotal: Long?,
    val genres: List<Genre>?,
    val episodes: List<Episode>?,
    @SerialName("added_in_users_favorites")
    val userFavourites: Long,
    @SerialName("average_duration_of_episode")
    val averageDuration: Long?,
    @SerialName("added_in_planned_collection")
    val planned: Long,
    @SerialName("added_in_watched_collection")
    val watched: Long,
    @SerialName("added_in_watching_collection")
    val watching: Long,
    @SerialName("added_in_postponed_collection")
    val postponed: Long,
    @SerialName("added_in_abandoned_collection")
    val abandoned: Long,
)
