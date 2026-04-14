package com.dezdeqness.analytics.core

interface AkaneAnalytics {
    fun trackFavouriteAnime(animeId: Long, title: String)

    fun trackUnfavouriteAnime(animeId: Long, title: String)

    fun trackEpisodeDownloadSucceeded(episodeId: String, animeId: Long, animeTitle: String)

    fun trackEpisodeDownloadFailed(episodeId: String, animeId: Long, animeTitle: String)

    fun trackPlayerStarted(episodeId: String, episodeTitle: String)

    fun trackEpisodeFinished(episodeId: String, episodeTitle: String)

    fun trackSearch(query: String)

    fun trackDetailsOpened(animeId: Long, title: String)

    fun trackBottomNavigation(route: String)
}