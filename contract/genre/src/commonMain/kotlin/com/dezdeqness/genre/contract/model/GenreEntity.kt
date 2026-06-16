package com.dezdeqness.genre.contract.model

data class GenreEntity(
    val id: Int,
    val name: String,
    val image: GenreImageEntity,
    val totalReleases: Int,
)
