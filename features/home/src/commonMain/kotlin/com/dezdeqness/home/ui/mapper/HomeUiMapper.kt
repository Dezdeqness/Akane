package com.dezdeqness.home.ui.mapper

import com.dezdeqness.calendar.contract.model.ScheduleItemEntity
import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.franchise.contract.model.FranchiseEntity
import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.home.ui.model.FranchisePanelUiModel
import com.dezdeqness.home.ui.model.GenrePanelUiModel
import com.dezdeqness.home.ui.model.HomeUiModel
import com.dezdeqness.home.ui.model.PromoPanelUiModel
import com.dezdeqness.promo.contract.model.PromoEntity

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
}
