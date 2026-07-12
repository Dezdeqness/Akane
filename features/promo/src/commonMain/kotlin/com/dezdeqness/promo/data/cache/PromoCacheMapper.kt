package com.dezdeqness.promo.data.cache

import com.dezdeqness.promo.contract.model.PromoEntity
import com.dezdeqness.promo.contract.model.PromoTarget

class PromoCacheMapper {

    fun toSnapshot(entity: PromoEntity) = PromoSnapshot(
        id = entity.id,
        imageUrl = entity.imageUrl,
        title = entity.title,
        description = entity.description,
        actionLabel = entity.actionLabel,
        isAd = entity.isAd,
        hasOverlay = entity.hasOverlay,
        targetType = when (entity.target) {
            is PromoTarget.Link -> TARGET_LINK
            is PromoTarget.Release -> TARGET_RELEASE
            PromoTarget.None -> TARGET_NONE
        },
        targetLink = (entity.target as? PromoTarget.Link)?.url,
        targetReleaseId = (entity.target as? PromoTarget.Release)?.releaseId,
    )

    fun toEntity(snapshot: PromoSnapshot) = PromoEntity(
        id = snapshot.id,
        imageUrl = snapshot.imageUrl,
        title = snapshot.title,
        description = snapshot.description,
        actionLabel = snapshot.actionLabel,
        isAd = snapshot.isAd,
        hasOverlay = snapshot.hasOverlay,
        target = when (snapshot.targetType) {
            TARGET_LINK -> snapshot.targetLink?.let(PromoTarget::Link) ?: PromoTarget.None
            TARGET_RELEASE -> snapshot.targetReleaseId?.let(PromoTarget::Release) ?: PromoTarget.None
            else -> PromoTarget.None
        },
    )

    private companion object {
        const val TARGET_LINK = "LINK"
        const val TARGET_RELEASE = "RELEASE"
        const val TARGET_NONE = "NONE"
    }
}
