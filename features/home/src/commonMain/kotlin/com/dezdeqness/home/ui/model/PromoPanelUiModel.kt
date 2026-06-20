package com.dezdeqness.home.ui.model

import com.dezdeqness.promo.contract.model.PromoTarget

data class PromoPanelUiModel(
    val id: String,
    val imageUrl: String,
    val title: String?,
    val actionLabel: String?,
    val isAd: Boolean,
    val hasOverlay: Boolean,
    val target: PromoTarget,
)
