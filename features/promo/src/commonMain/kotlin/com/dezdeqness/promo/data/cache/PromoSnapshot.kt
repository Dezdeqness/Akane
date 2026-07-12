package com.dezdeqness.promo.data.cache

import kotlinx.serialization.Serializable

@Serializable
data class PromoSnapshot(
    val id: String,
    val imageUrl: String,
    val title: String?,
    val description: String?,
    val actionLabel: String?,
    val isAd: Boolean,
    val hasOverlay: Boolean,
    val targetType: String,
    val targetLink: String?,
    val targetReleaseId: Long?,
)
