package com.dezdeqness.franchise.data.mapper

import com.dezdeqness.franchise.contract.model.FranchiseEntity
import com.dezdeqness.franchise.data.db.FranchiseLocal
import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.network.models.response.FranchiseItemResponse

class FranchiseMapper(
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(response: FranchiseItemResponse) = FranchiseEntity(
        id = response.id,
        name = response.name,
        nameEnglish = response.nameEnglish.orEmpty(),
        imageUrl = imageUrlBuilder.build(response.image.optimized.preview ?: response.image.preview),
        rating = response.rating ?: 0.0,
        firstYear = response.firstYear?.toInt() ?: 0,
        lastYear = response.lastYear?.toInt() ?: 0,
        totalReleases = response.totalReleases?.toInt() ?: 0,
        totalEpisodes = response.totalEpisodes?.toInt() ?: 0,
        totalDuration = response.totalDuration,
        totalDurationInSeconds = response.totalDurationInSeconds?.toInt() ?: 0,
    )

    fun toLocal(entity: FranchiseEntity) = FranchiseLocal(
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

    fun toEntity(local: FranchiseLocal) = FranchiseEntity(
        id = local.id,
        name = local.name,
        nameEnglish = local.nameEnglish,
        imageUrl = local.imageUrl,
        rating = local.rating,
        firstYear = local.firstYear,
        lastYear = local.lastYear,
        totalReleases = local.totalReleases,
        totalEpisodes = local.totalEpisodes,
        totalDuration = local.totalDuration,
        totalDurationInSeconds = local.totalDurationInSeconds,
    )
}
