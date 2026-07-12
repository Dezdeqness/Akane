package com.dezdeqness.genre.data.cache

import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.genre.contract.model.GenreImageEntity

class GenreCacheMapper {

    fun toSnapshot(entity: GenreEntity) = GenreSnapshot(
        id = entity.id,
        name = entity.name,
        image = GenreImageSnapshot(
            preview = entity.image.preview,
            thumbnail = entity.image.thumbnail,
            optimizedPreview = entity.image.optimizedPreview,
            optimizedThumbnail = entity.image.optimizedThumbnail,
        ),
        totalReleases = entity.totalReleases,
    )

    fun toEntity(snapshot: GenreSnapshot) = GenreEntity(
        id = snapshot.id,
        name = snapshot.name,
        image = GenreImageEntity(
            preview = snapshot.image.preview,
            thumbnail = snapshot.image.thumbnail,
            optimizedPreview = snapshot.image.optimizedPreview,
            optimizedThumbnail = snapshot.image.optimizedThumbnail,
        ),
        totalReleases = snapshot.totalReleases,
    )
}
