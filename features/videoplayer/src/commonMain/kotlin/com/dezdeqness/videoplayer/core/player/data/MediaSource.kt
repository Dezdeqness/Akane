package com.dezdeqness.videoplayer.core.player.data

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
