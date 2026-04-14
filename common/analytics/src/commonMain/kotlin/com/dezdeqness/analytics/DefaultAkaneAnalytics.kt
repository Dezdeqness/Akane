package com.dezdeqness.analytics

import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.analytics.core.Analytics
import com.dezdeqness.analytics.core.AnalyticsProperties
import com.dezdeqness.analytics.utils.asAnalyticsValue

class DefaultAkaneAnalytics(
    private val analytics: Analytics,
) : AkaneAnalytics {

    override fun trackFavouriteAnime(animeId: Long, title: String) {
        analytics.track(
            eventName = FAVOURITE_ANIME,
            properties = animeProps(animeId = animeId, title = title),
        )
    }

    override fun trackUnfavouriteAnime(animeId: Long, title: String) {
        analytics.track(
            eventName = UNFAVOURITE_ANIME,
            properties = animeProps(animeId = animeId, title = title),
        )
    }

    override fun trackEpisodeDownloadSucceeded(episodeId: String, animeId: Long, animeTitle: String) {
        analytics.track(
            eventName = DOWNLOAD_EPISODE_SUCCESS,
            properties = buildMap {
                put(EPISODE_ID, episodeId.asAnalyticsValue())
                putAll(animeProps(animeId = animeId, title = animeTitle))
            },
        )
    }

    override fun trackEpisodeDownloadFailed(episodeId: String, animeId: Long, animeTitle: String) {
        analytics.track(
            eventName = DOWNLOAD_EPISODE_ERROR,
            properties = buildMap {
                put(EPISODE_ID, episodeId.asAnalyticsValue())
                putAll(animeProps(animeId = animeId, title = animeTitle))
            },
        )
    }

    override fun trackPlayerStarted(episodeId: String, episodeTitle: String) {
        analytics.track(
            eventName = PLAYER_STARTED,
            properties = episodeProps(episodeId = episodeId, episodeTitle = episodeTitle),
        )
    }

    override fun trackEpisodeFinished(episodeId: String, episodeTitle: String) {
        analytics.track(
            eventName = EPISODE_FINISHED,
            properties = episodeProps(episodeId = episodeId, episodeTitle = episodeTitle),
        )
    }

    override fun trackSearch(query: String) {
        if (query.isBlank()) return

        analytics.track(
            eventName = SEARCH,
            properties = mapOf(QUERY to query.asAnalyticsValue()),
        )
    }

    override fun trackDetailsOpened(animeId: Long, title: String) {
        analytics.track(
            eventName = DETAILS_OPENED,
            properties = animeProps(animeId = animeId, title = title),
        )
    }

    override fun trackBottomNavigation(route: String) {
        if (route.isBlank()) return

        analytics.track(
            eventName = BOTTOM_NAVIGATION,
            properties = mapOf(ROUTE to route.asAnalyticsValue()),
        )
    }

    private fun animeProps(animeId: Long, title: String): AnalyticsProperties = mapOf(
        ANIME_ID to animeId.asAnalyticsValue(),
        ANIME_TITLE to title.asAnalyticsValue(),
    )

    private fun episodeProps(episodeId: String, episodeTitle: String): AnalyticsProperties = mapOf(
        EPISODE_ID to episodeId.asAnalyticsValue(),
        EPISODE_TITLE to episodeTitle.asAnalyticsValue(),
    )

    private companion object {
        const val FAVOURITE_ANIME = "favourite_anime"
        const val UNFAVOURITE_ANIME = "unfavourite_anime"
        const val DOWNLOAD_EPISODE_SUCCESS = "download_episode_success"
        const val DOWNLOAD_EPISODE_ERROR = "download_episode_error"
        const val PLAYER_STARTED = "player_started"
        const val EPISODE_FINISHED = "episode_finished"
        const val SEARCH = "search"
        const val DETAILS_OPENED = "details_opened"
        const val BOTTOM_NAVIGATION = "bottom_navigation"

        const val ANIME_ID = "animeId"
        const val ANIME_TITLE = "title"
        const val EPISODE_ID = "episodeId"
        const val EPISODE_TITLE = "episodeTitle"
        const val QUERY = "query"
        const val ROUTE = "route"
    }
}
