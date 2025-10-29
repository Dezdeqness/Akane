package com.dezdeqness.network.models.core

import kotlinx.serialization.Serializable

@Serializable
data class Poster(
    val src: String,
    val thumbnail: String,
    val optimized: Optimized,
)
