package com.dezdeqness.videoplayer.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VideoPlayerViewModel(
) : ViewModel() {

    private val _videoPlayerStateFlow: MutableStateFlow<VideoPlayerState> = MutableStateFlow(
        VideoPlayerState()
    )
    val videoPlayerStateFlow: StateFlow<VideoPlayerState> = _videoPlayerStateFlow

    companion object {
        private const val TAG = "VideoPlayerViewModel"
    }
}
