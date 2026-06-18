package com.dezdeqness.franchise.contract.model

import com.dezdeqness.catalog.contract.model.ReleaseEntity

data class FranchiseReleaseEntity(
    val id: String,
    val sortOrder: Int,
    val releaseId: Long,
    val franchiseId: String,
    val release: ReleaseEntity,
)
