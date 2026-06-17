package com.dezdeqness.genre.data.mapper

import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.genre.contract.model.GenreImageEntity
import com.dezdeqness.network.constants.BaseUrl
import com.dezdeqness.network.models.core.Genre

class GenreMapper {

    fun map(response: Genre) = GenreEntity(
        id = response.id.toInt(),
        name = response.name,
        image = GenreImageEntity(
            preview = BaseUrl.BASE_URL_IMAGES + response.image.preview,
            thumbnail = BaseUrl.BASE_URL_IMAGES + response.image.thumbnail,
            optimizedPreview = BaseUrl.BASE_URL_IMAGES + response.image.optimized.preview.orEmpty(),
            optimizedThumbnail = BaseUrl.BASE_URL_IMAGES + response.image.optimized.thumbnail.orEmpty(),
        ),
        totalReleases = response.totalReleases.toInt(),
    )

}
