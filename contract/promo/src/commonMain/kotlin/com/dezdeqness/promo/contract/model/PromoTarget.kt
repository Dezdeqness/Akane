package com.dezdeqness.promo.contract.model

sealed interface PromoTarget {
    data class Link(val url: String) : PromoTarget
    data class Release(val releaseId: Long) : PromoTarget
    data object None : PromoTarget
}
