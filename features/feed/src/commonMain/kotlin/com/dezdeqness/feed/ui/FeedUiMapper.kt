package com.dezdeqness.feed.ui

import com.dezdeqness.feed.domain.model.ReleaseEntity
import com.dezdeqness.feed.ui.model.FeedAnimeUiModel

class FeedUiMapper {

    fun map(item: ReleaseEntity) = FeedAnimeUiModel(
        id = item.id,
        title = item.name,
        summary = item.description,
        imageUrl = item.poster,
    )

}
