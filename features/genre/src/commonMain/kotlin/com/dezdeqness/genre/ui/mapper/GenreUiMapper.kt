package com.dezdeqness.genre.ui.mapper

import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.genre.ui.model.GenreUiModel

class GenreUiMapper {

    fun map(entity: GenreEntity) = GenreUiModel(
        id = entity.id,
        name = entity.name,
        imageUrl = entity.image.optimizedPreview.ifEmpty { entity.image.preview },
        totalReleases = entity.totalReleases,
    )

}
