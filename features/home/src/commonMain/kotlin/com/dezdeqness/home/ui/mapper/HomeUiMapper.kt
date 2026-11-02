package com.dezdeqness.home.ui.mapper

import com.dezdeqness.calendar.contract.model.ScheduleItemEntity
import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.franchise.contract.model.FranchiseEntity
import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.home.domain.ContinueWatchingEntity
import com.dezdeqness.home.ui.model.ContinueWatchingUiModel
import com.dezdeqness.home.ui.model.FranchisePanelUiModel
import com.dezdeqness.home.ui.model.GenrePanelUiModel
import com.dezdeqness.home.ui.model.HomeUiModel
import com.dezdeqness.home.ui.model.PromoPanelUiModel
import com.dezdeqness.home.ui.model.StoryCollectionPreviewUiModel
import com.dezdeqness.promo.contract.model.PromoEntity
import com.dezdeqness.stories.contract.model.StoryCollectionEntity

class HomeUiMapper {

    fun toFranchisePanel(entity: FranchiseEntity) =
        FranchisePanelUiModel(
            id = entity.id,
            name = entity.name,
            imageUrl = entity.imageUrl,
            totalReleases = entity.totalReleases,
        )

    fun toGenrePanel(entity: GenreEntity) =
        GenrePanelUiModel(
            id = entity.id,
            name = entity.name,
            imageUrl = entity.image.optimizedPreview.ifEmpty { entity.image.preview },
            totalReleases = entity.totalReleases,
        )

    fun toPromoPanel(entity: PromoEntity) =
        PromoPanelUiModel(
            id = entity.id,
            imageUrl = entity.imageUrl,
            title = entity.title,
            actionLabel = entity.actionLabel,
            isAd = entity.isAd,
            hasOverlay = entity.hasOverlay,
            target = entity.target,
        )
    fun toUiModel(data: ReleaseEntity) =
        HomeUiModel(
            id = data.id,
            name = data.name,
            imagePath = data.poster,
            description = data.description,
        )

    fun toUiModelSchedule(data: ScheduleItemEntity) =
        HomeUiModel(
            id = data.id,
            name = data.name,
            imagePath = data.poster,
            description = data.description,
        )

    fun toStoryCollectionPreview(index: Int, entity: StoryCollectionEntity) =
        StoryCollectionPreviewUiModel(
            index = index,
            title = when (entity.dayOffset) {
                0 -> "Сегодня"
                1 -> "Вчера"
                else -> STORY_DAY_NAMES.getOrElse(entity.dayOfWeek - 1) { "" }
            },
            coverUrl = entity.stories.firstOrNull()?.poster.orEmpty(),
            storiesCount = entity.stories.size,
        )

    fun toContinueWatching(entity: ContinueWatchingEntity) =
        ContinueWatchingUiModel(
            releaseId = entity.releaseId,
            episodeId = entity.episodeId,
            title = entity.releaseTitle,
            episodeTitle = entity.episodeName.ifBlank { "Серия ${entity.episodeOrdinal}" },
            imagePath = entity.previewUrl,
        )

    private companion object {
        val STORY_DAY_NAMES = listOf(
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
