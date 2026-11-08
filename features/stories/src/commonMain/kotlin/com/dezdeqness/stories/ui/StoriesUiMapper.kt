package com.dezdeqness.stories.ui

import com.dezdeqness.foundation.utils.toEpisodeOrdinalString
import com.dezdeqness.stories.contract.model.StoryCollectionEntity
import com.dezdeqness.stories.contract.model.StoryEntity
import com.dezdeqness.stories.ui.model.StoryCollectionUiModel
import com.dezdeqness.stories.ui.model.StoryUiModel

class StoriesUiMapper {

    fun map(entity: StoryCollectionEntity) = StoryCollectionUiModel(
        id = entity.dayOfWeek,
        title = collectionTitle(entity.dayOffset, entity.dayOfWeek),
        stories = entity.stories.map(::map),
    )

    private fun map(entity: StoryEntity) = StoryUiModel(
        releaseId = entity.releaseId,
        releaseName = entity.releaseName,
        poster = entity.poster,
        episodeId = entity.episodeId,
        episodeLabel = if (entity.episodeName.isBlank()) {
            "${entity.episodeOrdinal.toEpisodeOrdinalString()} серия"
        } else {
            "${entity.episodeOrdinal.toEpisodeOrdinalString()} серия — ${entity.episodeName}"
        },
        previewUrl = entity.previewUrl,
        videoUrl = entity.videoUrl,
    )

    private fun collectionTitle(dayOffset: Int, dayOfWeek: Int) = when (dayOffset) {
        0 -> "Сегодня"
        1 -> "Вчера"
        else -> DAY_NAMES.getOrElse(dayOfWeek - 1) { "" }
    }

    private companion object {
        val DAY_NAMES = listOf(
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота",
            "Воскресенье",
        )
    }
}
