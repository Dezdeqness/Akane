package com.dezdeqness.network.models.core.release

import kotlinx.serialization.Serializable

@Serializable
data class Type(
    val value: String?,
    val description: String?,
)
