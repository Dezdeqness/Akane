package com.dezdeqness.promo.data.mapper

import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.network.models.response.PromotionResponse
import com.dezdeqness.promo.contract.model.PromoEntity
import com.dezdeqness.promo.contract.model.PromoTarget

class PromoMapper(
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(response: PromotionResponse) = PromoEntity(
        id = response.id,
        imageUrl = imageUrlBuilder.build(response.image.optimized.preview ?: response.image.preview),
        title = response.title,
        description = response.description,
        actionLabel = response.urlLabel,
        isAd = response.isAd,
        hasOverlay = response.hasOverlay,
        target = resolveTarget(response),
    )

    private fun resolveTarget(response: PromotionResponse): PromoTarget {
        val url = response.url
        val release = response.release
        return when {
            !url.isNullOrBlank() -> PromoTarget.Link(url)
            release != null -> PromoTarget.Release(release.id)
            else -> PromoTarget.None
        }
    }
}
