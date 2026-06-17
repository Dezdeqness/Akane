package com.dezdeqness.home.ui.mapper

import com.dezdeqness.calendar.contract.model.ScheduleItemEntity
import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.home.ui.model.GenrePanelUiModel
import com.dezdeqness.home.ui.model.HomeUiModel

class HomeUiMapper {

    fun toGenrePanel(entity: GenreEntity) =
        GenrePanelUiModel(
            id = entity.id,
            name = entity.name,
            imageUrl = entity.image.optimizedPreview.ifEmpty { entity.image.preview },
            totalReleases = entity.totalReleases,
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
