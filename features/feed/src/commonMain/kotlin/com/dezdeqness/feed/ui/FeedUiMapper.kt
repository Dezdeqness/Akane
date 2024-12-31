package com.dezdeqness.feed.ui

import com.dezdeqness.feed.domain.model.ReleaseEntity
import com.dezdeqness.feed.ui.model.FeedAnimeUiModel
import com.dezdeqness.network.constants.ApiEndPoints
import com.dezdeqness.network.constants.BaseUrl

class FeedUiMapper {

    fun map(item: ReleaseEntity) = FeedAnimeUiModel(
        id = item.id,
        title = item.name,
        summary = item.description,
        imageUrl = BaseUrl.BASE_URL_IMAGES + item.poster,
    )

}
