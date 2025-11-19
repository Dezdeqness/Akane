package com.dezdeqness.feed.data.mapper

import com.dezdeqness.feed.data.builder.CatalogFilterBuilder
import com.dezdeqness.feed.domain.model.AgeRating
import com.dezdeqness.feed.domain.model.CatalogFilter

class CatalogFilterMapper {
    fun map(filter: CatalogFilter): CatalogFilterBuilder = CatalogFilterBuilder().apply {
        filter.page?.let { page(it) }
        filter.limit?.let { limit(it) }

        if (filter.genres.isNotEmpty()) {
            genres(filter.genres)
        }

        if (filter.types.isNotEmpty()) {
            types(filter.types.map { it.name })
        }

        if (filter.seasons.isNotEmpty()) {
            seasons(filter.seasons.map { season ->
                season.apiValue
            })
        }

        fromYear(filter.fromYear)
        toYear(filter.toYear)
        search(filter.search)

        sorting(filter.sorting?.name)

        if (filter.ageRatings.isNotEmpty()) {
            ageRatings(filter.ageRatings.map(AgeRating::name))
        }

        publishStatus(filter.publishStatus?.name)

        productionStatus(filter.productionStatus?.name)

        if (filter.include.isNotEmpty()) {
            include(filter.include)
        }

        if (filter.exclude.isNotEmpty()) {
            exclude(filter.exclude)
        }
    }
}
