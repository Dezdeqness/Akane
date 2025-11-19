package com.dezdeqness.feed.domain.model

data class CatalogFilter(
    val page: Int? = null,
    val limit: Int? = null,
    val genres: List<Int> = emptyList(),
    val types: List<ReleaseType> = emptyList(),
    val seasons: List<Season> = emptyList(),
    val fromYear: Int? = null,
    val toYear: Int? = null,
    val search: String? = null,
    val sorting: Sorting? = null,
    val ageRatings: List<AgeRating> = emptyList(),
    val publishStatus: PublishStatus? = null,
    val productionStatus: ProductionStatus? = null,
    val include: List<String> = emptyList(),
    val exclude: List<String> = emptyList(),
)

enum class ReleaseType {
    TV,
    ONA,
    WEB,
    OVA,
    OAD,
    MOVIE,
    DORAMA,
    SPECIAL,
}

enum class Season(val apiValue: String) {
    WINTER("winter"),
    SPRING("spring"),
    SUMMER("summer"),
    AUTUMN("autumn"),
}

enum class Sorting {
    FRESH_AT_DESC,
    FRESH_AT_ASC,
    RATING_DESC,
    RATING_ASC,
    YEAR_DESC,
    YEAR_ASC,
}

enum class AgeRating {
    R0_PLUS,
    R6_PLUS,
    R12_PLUS,
    R16_PLUS,
    R18_PLUS,
}

enum class PublishStatus {
    IS_ONGOING,
    IS_NOT_ONGOING,
}

enum class ProductionStatus {
    IS_IN_PRODUCTION,
    IS_NOT_IN_PRODUCTION,
}
