package com.dezdeqness.promo.contract.model

data class PromoEntity(
    val id: String,
    val imageUrl: String,
    val title: String?,
    val description: String?,
    val actionLabel: String?,
    val isAd: Boolean,
    val hasOverlay: Boolean,
    val target: PromoTarget,
)
