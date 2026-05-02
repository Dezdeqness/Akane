package com.dezdeqness.videoplayer.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

interface VideoPlayerNavigationController {
    fun navigateToPlayer(backStack: NavBackStack<NavKey>, id: Long, episodeId: String)
    fun navigateToDownloadedPlaylist(backStack: NavBackStack<NavKey>, releaseId: Long, startEpisodeId: String)
}
