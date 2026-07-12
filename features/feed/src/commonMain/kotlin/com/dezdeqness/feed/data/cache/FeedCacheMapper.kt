package com.dezdeqness.feed.data.cache

import com.dezdeqness.catalog.contract.model.ReleaseEntity

class FeedCacheMapper {

    fun toSnapshot(entity: ReleaseEntity) = ReleaseSnapshot(
        id = entity.id,
        name = entity.name,
        poster = entity.poster,
        type = entity.type,
        description = entity.description,
    )

    fun toEntity(snapshot: ReleaseSnapshot) = ReleaseEntity(
        id = snapshot.id,
        name = snapshot.name,
        poster = snapshot.poster,
        type = snapshot.type,
        description = snapshot.description,
    )
}
