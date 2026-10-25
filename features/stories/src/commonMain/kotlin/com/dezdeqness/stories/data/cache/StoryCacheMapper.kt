package com.dezdeqness.stories.data.cache

import com.dezdeqness.stories.contract.model.StoryCollectionEntity
import com.dezdeqness.stories.contract.model.StoryEntity

class StoryCacheMapper {

    fun toSnapshot(entity: StoryCollectionEntity) = StoryCollectionSnapshot(
        dayOfWeek = entity.dayOfWeek,
        dayOffset = entity.dayOffset,
        stories = entity.stories.map(::toSnapshot),
    )

    fun toEntity(snapshot: StoryCollectionSnapshot) = StoryCollectionEntity(
        dayOfWeek = snapshot.dayOfWeek,
        dayOffset = snapshot.dayOffset,
        stories = snapshot.stories.map(::toEntity),
    )

    private fun toSnapshot(entity: StoryEntity) = StorySnapshot(
        releaseId = entity.releaseId,
        releaseName = entity.releaseName,
        poster = entity.poster,
        episodeId = entity.episodeId,
        episodeOrdinal = entity.episodeOrdinal,
        episodeName = entity.episodeName,
        previewUrl = entity.previewUrl,
        videoUrl = entity.videoUrl,
    )

    private fun toEntity(snapshot: StorySnapshot) = StoryEntity(
        releaseId = snapshot.releaseId,
        releaseName = snapshot.releaseName,
        poster = snapshot.poster,
        episodeId = snapshot.episodeId,
        episodeOrdinal = snapshot.episodeOrdinal,
        episodeName = snapshot.episodeName,
        previewUrl = snapshot.previewUrl,
        videoUrl = snapshot.videoUrl,
    )
}
