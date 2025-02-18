package com.dezdeqness.videoplayer.core

import com.dezdeqness.videoplayer.navigation.AndroidVideoPlayerNavigationController
import com.dezdeqness.videoplayer.navigation.VideoPlayerNavigationController

actual fun videoController(): VideoPlayerNavigationController =
    AndroidVideoPlayerNavigationController()
