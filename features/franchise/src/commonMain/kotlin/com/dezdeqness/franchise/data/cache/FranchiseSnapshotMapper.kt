package com.dezdeqness.franchise.data.cache

import com.dezdeqness.franchise.contract.model.FranchiseEntity

class FranchiseSnapshotMapper {

    fun toSnapshot(entity: FranchiseEntity) = FranchiseSnapshot(
        id = entity.id,
        name = entity.name,
        nameEnglish = entity.nameEnglish,
        imageUrl = entity.imageUrl,
        rating = entity.rating,
        firstYear = entity.firstYear,
        lastYear = entity.lastYear,
        totalReleases = entity.totalReleases,
        totalEpisodes = entity.totalEpisodes,
        totalDuration = entity.totalDuration,
        totalDurationInSeconds = entity.totalDurationInSeconds,
    )

    fun toEntity(snapshot: FranchiseSnapshot) = FranchiseEntity(
        id = snapshot.id,
        name = snapshot.name,
        nameEnglish = snapshot.nameEnglish,
        imageUrl = snapshot.imageUrl,
        rating = snapshot.rating,
        firstYear = snapshot.firstYear,
        lastYear = snapshot.lastYear,
        totalReleases = snapshot.totalReleases,
        totalEpisodes = snapshot.totalEpisodes,
        totalDuration = snapshot.totalDuration,
        totalDurationInSeconds = snapshot.totalDurationInSeconds,
    )
}
