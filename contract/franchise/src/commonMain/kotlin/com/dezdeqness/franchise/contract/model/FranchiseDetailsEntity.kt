package com.dezdeqness.franchise.contract.model

data class FranchiseDetailsEntity(
    val franchise: FranchiseEntity,
    val releases: List<FranchiseReleaseEntity>,
)
