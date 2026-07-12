package com.dezdeqness.feed.data.cache

import kotlinx.serialization.Serializable

@Serializable
data class ReleaseSnapshot(
    val id: Long,
    val name: String,
    val poster: String,
    val type: String,
    val description: String,
)
