package com.dezdeqness.details.domain.model

data class FranchiseEntity(
    val id: String,
    val name: String,
    val nameEnglish: String,
    val firstYear: Long,
    val lastYear: Long,
    val totalEpisodes: Long,
    val totalReleases: Long,
    val totalDuration: String,
    val totalDurationInSeconds: Long,
    val franchiseReleases: List<FranchiseReleaseEntity>,
)

data class FranchiseReleaseEntity(
    val id: String,
    val sortOrder: Long,
    val releaseId: Long,
    val franchiseId: String,
    val release: ReleaseDetailsEntity,
)
