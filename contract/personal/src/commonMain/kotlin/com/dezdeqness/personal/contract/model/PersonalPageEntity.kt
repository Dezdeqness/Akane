package com.dezdeqness.personal.contract.model

data class PersonalPageEntity(
    val items: List<PersonalEntity>,
    val currentPage: Int,
    val nextPage: Int,
    val hasNextPage: Boolean,
)
