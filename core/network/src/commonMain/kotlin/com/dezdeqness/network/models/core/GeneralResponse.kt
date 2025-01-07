package com.dezdeqness.network.models.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeneralResponse<T>(
    val data: T?,
    val meta: Meta,
)

@Serializable
data class Meta(
    val pagination: Pagination,
)

@Serializable
data class Pagination(
    val total: Int,
    val count: Int,
    @SerialName("per_page")
    val perPage: Long,
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("total_pages")
    val totalPages: Int,
)
