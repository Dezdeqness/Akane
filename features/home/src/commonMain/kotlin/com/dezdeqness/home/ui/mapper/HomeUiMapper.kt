package com.dezdeqness.home.ui.mapper

import com.dezdeqness.feed.domain.model.ReleaseEntity
import com.dezdeqness.home.ui.model.HomeUiModel

class HomeUiMapper {
    fun toUiModel(data: ReleaseEntity) =
        HomeUiModel(
            id = data.id,
            name = data.name,
            imagePath = data.poster,
        )
}
