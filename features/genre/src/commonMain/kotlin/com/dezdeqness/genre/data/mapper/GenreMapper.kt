package com.dezdeqness.genre.data.mapper

import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.genre.contract.model.GenreImageEntity
import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.network.models.core.Genre

class GenreMapper(
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(response: Genre) = GenreEntity(
        id = response.id.toInt(),
        name = response.name,
        image = GenreImageEntity(
            preview = imageUrlBuilder.build(response.image.preview),
            thumbnail = imageUrlBuilder.build(response.image.thumbnail),
            optimizedPreview = imageUrlBuilder.build(response.image.optimized.preview),
            optimizedThumbnail = imageUrlBuilder.build(response.image.optimized.thumbnail),
        ),
        totalReleases = response.totalReleases.toInt(),
    )

}
