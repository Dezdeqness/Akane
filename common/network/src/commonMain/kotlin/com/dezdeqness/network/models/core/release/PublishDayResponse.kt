package com.dezdeqness.network.models.core.release

import kotlinx.serialization.Serializable

@Serializable
data class PublishDay(
    val value: Long,
    val description: String,
)
