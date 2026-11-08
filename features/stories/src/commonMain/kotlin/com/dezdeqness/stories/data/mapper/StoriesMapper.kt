package com.dezdeqness.stories.data.mapper

import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.network.models.response.PublishedEpisode
import com.dezdeqness.network.models.response.ScheduleItemResponse
import com.dezdeqness.stories.contract.model.StoryCollectionEntity
import com.dezdeqness.stories.contract.model.StoryEntity

class StoriesMapper(
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(response: List<ScheduleItemResponse>, todayDayOfWeek: Int): List<StoryCollectionEntity> {
        val storiesByDay = response
            .mapNotNull { item ->
                val episode = item.publishedReleaseEpisode ?: return@mapNotNull null
                val story = mapStory(item, episode) ?: return@mapNotNull null
                Triple(item.release.publishDay.value.toInt(), episode.updatedAt, story)
            }
            .groupBy(keySelector = { it.first })

        return (0 until DAYS_IN_WEEK).mapNotNull { offset ->
            val dayOfWeek = ((todayDayOfWeek - 1 - offset) % DAYS_IN_WEEK + DAYS_IN_WEEK) % DAYS_IN_WEEK + 1
            val stories = storiesByDay[dayOfWeek].orEmpty()
                .sortedByDescending { it.second }
                .take(MAX_STORIES_PER_DAY)
                .map { it.third }

            if (stories.isEmpty()) {
                null
            } else {
                StoryCollectionEntity(
                    dayOfWeek = dayOfWeek,
                    dayOffset = offset,
                    stories = stories,
                )
            }
        }
    }

    private fun mapStory(item: ScheduleItemResponse, episode: PublishedEpisode): StoryEntity? {
        val videoUrl = episode.hls480 ?: episode.hls720 ?: episode.hls1080 ?: return null
        return StoryEntity(
            releaseId = item.release.id,
            releaseName = item.release.name.main,
            poster = imageUrlBuilder.build(item.release.poster.optimized.src ?: item.release.poster.src),
            episodeId = episode.id,
            episodeOrdinal = episode.ordinal,
            episodeName = episode.name.orEmpty(),
            previewUrl = imageUrlBuilder.build(episode.preview.optimized.src ?: episode.preview.src),
            videoUrl = videoUrl,
        )
    }

    private companion object {
        const val DAYS_IN_WEEK = 7
        const val MAX_STORIES_PER_DAY = 10
    }
}
