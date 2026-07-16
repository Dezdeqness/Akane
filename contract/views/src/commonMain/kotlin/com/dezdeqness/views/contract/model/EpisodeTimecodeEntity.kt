package com.dezdeqness.views.contract.model

data class EpisodeTimecodeEntity(
    val releaseEpisodeId: String,
    val timeMs: Long,
    val isWatched: Boolean,
) {
    fun fractionOf(episodeDurationMs: Long): Float? = when {
        isWatched -> 1f
        episodeDurationMs > 0L && timeMs > 0L -> (timeMs.toFloat() / episodeDurationMs).coerceIn(0f, 1f)
        else -> null
    }

}
