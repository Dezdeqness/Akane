package com.dezdeqness.videoplayer.core.player.data

import com.dezdeqness.details.domain.model.VideoQuality

sealed interface MediaSource {

    data class Url(val url: String) : MediaSource

    data class MultiQuality(
        val variants: List<QualityVariant>,
    ) : MediaSource

    data class FilePath(val path: String) : MediaSource
}

data class QualityVariant(
    val quality: MediaQuality,
    val url: String,
)

fun VideoQuality.toTransformToMediaQuality(): MediaQuality {
    return when (this) {
        VideoQuality.q480 -> MediaQuality.q480
        VideoQuality.q720 -> MediaQuality.q720
        VideoQuality.q1080 -> MediaQuality.q1080
    }
}

internal fun MediaSource.resolveUrl(selectedQuality: MediaQuality): String = when (this) {
    is MediaSource.Url -> url
    is MediaSource.FilePath -> path
    is MediaSource.MultiQuality -> {
        val variant = variants.firstOrNull { it.quality == selectedQuality } ?: variants.lastOrNull()
        variant?.url.orEmpty()
    }
}

fun MediaSource.qualityVariants(): List<QualityVariant> =
    (this as? MediaSource.MultiQuality)?.variants ?: emptyList()
