package com.dezdeqness.network.models.response

import com.dezdeqness.network.models.core.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromotionResponse(
    val id: String,
    val url: String? = null,
    val image: Image,
    val title: String? = null,
    @SerialName("is_ad")
    val isAd: Boolean = false,
    @SerialName("url_label")
    val urlLabel: String? = null,
    val description: String? = null,
    @SerialName("has_overlay")
    val hasOverlay: Boolean = false,
    val release: ReleaseResponse? = null,
)
