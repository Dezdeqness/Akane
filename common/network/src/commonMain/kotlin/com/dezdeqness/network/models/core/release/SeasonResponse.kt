package com.dezdeqness.network.models.core.release

import kotlinx.serialization.Serializable

@Serializable
data class Season(
    val value: String,
    val description: String,
)
