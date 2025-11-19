package com.dezdeqness.feed.data.builder

import com.dezdeqness.network.constants.ApiParams

class CatalogFilterBuilder {

    var page: Int? = null
        private set

    var limit: Int? = null
        private set

    private val genres = mutableListOf<Int>()
    private val types = mutableListOf<String>()
    private val seasons = mutableListOf<String>()
    private var fromYear: Int? = null
    private var toYear: Int? = null
    private var search: String? = null
    private var sorting: String? = null
    private val ageRatings = mutableListOf<String>()
    private var publishStatus: String? = null
    private var productionStatus: String? = null
    private val include = mutableListOf<String>()
    private val exclude = mutableListOf<String>()

    fun page(value: Int) = apply { page = value }
    fun limit(value: Int) = apply { limit = value }

    fun genres(ids: List<Int>) = apply { genres += ids }
    fun types(values: List<String>) = apply { types += values }
    fun seasons(values: List<String>) = apply { seasons += values }

    fun fromYear(value: Int?) = apply { fromYear = value }
    fun toYear(value: Int?) = apply { toYear = value }

    fun search(value: String?) = apply { search = value }

    fun sorting(value: String?) = apply { sorting = value }

    fun ageRatings(values: List<String>) = apply { ageRatings += values }
    fun publishStatus(value: String?) = apply { publishStatus = value }
    fun productionStatus(value: String?) = apply { productionStatus = value }

    fun include(fields: List<String>) = apply { include += fields }
    fun exclude(fields: List<String>) = apply { exclude += fields }

    fun buildQueryMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        if (genres.isNotEmpty()) {
            map[ApiParams.QUERY_GENRES] = genres.joinToString(",")
        }

        if (types.isNotEmpty()) {
            map[ApiParams.QUERY_TYPES] = types.joinToString(",")
        }

        if (seasons.isNotEmpty()) {
            map[ApiParams.QUERY_SEASONS] = seasons.joinToString(",") { it.lowercase() }
        }

        fromYear?.let { map[ApiParams.QUERY_YEARS_FROM] = it }
        toYear?.let { map[ApiParams.QUERY_YEARS_TO] = it }

        search?.takeIf { it.isNotBlank() }?.let {
            map[ApiParams.QUERY_SEARCH] = it
        }

        sorting?.let {
            map[ApiParams.QUERY_SORTING] = it
        }

        if (ageRatings.isNotEmpty()) {
            map[ApiParams.QUERY_AGE_RATINGS] = ageRatings.joinToString(",")
        }

        publishStatus?.let {
            map[ApiParams.QUERY_PUBLISH_STATUSES] = it
        }

        productionStatus?.let {
            map[ApiParams.QUERY_PRODUCTION_STATUSES] = it
        }

        if (include.isNotEmpty()) {
            map[ApiParams.QUERY_INCLUDE] = include.joinToString(",")
        }

        if (exclude.isNotEmpty()) {
            map[ApiParams.QUERY_EXCLUDE] = exclude.joinToString(",")
        }

        return map
    }
}
