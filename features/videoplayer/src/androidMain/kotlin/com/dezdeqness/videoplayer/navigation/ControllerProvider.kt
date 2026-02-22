package com.dezdeqness.videoplayer.navigation

actual fun videoController(): VideoPlayerNavigationController =
    AndroidVideoPlayerNavigationController()
