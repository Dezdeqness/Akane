package com.dezdeqness.feed.data.mapper

import com.dezdeqness.feed.domain.model.ReleaseEntity
import com.dezdeqness.network.models.response.FeedResponse

class FeedMapper {

    fun map(response: FeedResponse): ReleaseEntity? {
        val item = response.release ?: return null

        return ReleaseEntity(
            id = item.id,
            code = item.code,
            names = item.names,
            series = item.series,
            poster = item.poster,
            last = item.last,
            status = item.status,
            statusCode = item.statusCode,
            type = item.type,
            genres = item.genres,
            voices = item.voices,
            year = item.year,
            season = item.season,
            day = item.day,
            description = item.description,
        )

    }
}
