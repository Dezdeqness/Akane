package com.dezdeqness.network.models.core.release

import kotlinx.serialization.Serializable

@Serializable
data class Name(
    val main: String,
    val english: String,
    val alternative: String?,
)
