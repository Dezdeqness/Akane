package com.dezdeqness.network.models.core.release

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgeRating(
    val value: String,
    val label: String,
    @SerialName("is_adult")
    val isAdult: Boolean,
    val description: String,
)
