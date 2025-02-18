package com.dezdeqness.videoplayer.core

import com.dezdeqness.videoplayer.navigation.VideoPlayerNavigationController
import com.dezdeqness.videoplayer.navigation.VideoPlayerNavigationControllerImpl

actual fun videoController(): VideoPlayerNavigationController =
    VideoPlayerNavigationControllerImpl()
