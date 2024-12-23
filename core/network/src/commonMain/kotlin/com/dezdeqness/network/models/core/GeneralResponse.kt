package com.dezdeqness.network.models.core

import kotlinx.serialization.Serializable

@Serializable
data class GeneralResponse<T>(
    val status: Boolean,
    val data: T?,
    val error: ErrorResponse? = null,
)

@Serializable
data class ErrorResponse(
    val code: Int,
    val message: String,
    val description: String? = null,
)
