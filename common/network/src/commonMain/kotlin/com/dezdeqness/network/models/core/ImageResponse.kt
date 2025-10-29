package com.dezdeqness.network.models.core

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val preview: String,
    val thumbnail: String,
    val optimized: Optimized,
)

@Serializable
data class Optimized(
    val src: String?,
    val thumbnail: String?,
)
