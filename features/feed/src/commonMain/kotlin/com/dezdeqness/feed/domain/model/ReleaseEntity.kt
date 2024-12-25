package com.dezdeqness.feed.domain.model

// TODO: shared
data class ReleaseEntity(
    val id: Long,
    val code: String,
    val names: List<String>,
    val series: String,
    val poster: String,
    val last: String,
    val status: String,
    val statusCode: String,
    val type: String,
    val genres: List<String>,
    val voices: List<String>,
    val year: String,
    val season: String,
    val day: String,
    val description: String,
)
