package com.dezdeqness.feed.data.mapper

import com.dezdeqness.feed.domain.model.ReleaseEntity
import com.dezdeqness.network.models.response.ReleaseResponse

class FeedMapper {

    fun map(response: ReleaseResponse) =
        ReleaseEntity(
            id = response.id,
            name = response.name.main,
            poster = response.poster.src,
            type = response.type.value,
            description = response.description.orEmpty(),
        )

}
