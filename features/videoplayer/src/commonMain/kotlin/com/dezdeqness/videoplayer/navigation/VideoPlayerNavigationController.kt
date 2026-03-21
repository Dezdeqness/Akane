package com.dezdeqness.videoplayer.navigation

import androidx.navigation.NavHostController

interface VideoPlayerNavigationController {
    fun navigateToPlayer(controller: NavHostController, id: Long, episodeId: String)
    fun navigateToDownloadedPlaylist(controller: NavHostController, releaseId: Long, startEpisodeId: String)
}
