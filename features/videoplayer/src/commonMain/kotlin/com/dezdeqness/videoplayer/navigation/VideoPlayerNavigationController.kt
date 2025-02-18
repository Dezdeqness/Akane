package com.dezdeqness.videoplayer.navigation

import androidx.navigation.NavHostController

interface VideoPlayerNavigationController {
    fun navigateToPlayer(controller: NavHostController, url: String)
}
