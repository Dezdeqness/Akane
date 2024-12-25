package com.dezdeqness.network.constants

object ApiParams {
    object FeedParams {
        fun createFeedParams(page: Int) =
            mapOf(
                "query" to "feed",
                "page" to page.toString(),
                "filter" to "id,torrents,playlist,externalPlaylist,favorite,moon,blockedInfo",
                "rm" to true,
            )
    }

}
