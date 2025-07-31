package com.dezdeqness.videoplayer.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.details.navigation.RELEASE_ID
import com.dezdeqness.details.ui.ReleaseDetailsUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VideoPlayerViewModel(
    private val releaseRepository: ReleaseRepository,
    private val releaseDetailsUiMapper: ReleaseDetailsUiMapper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var releaseId = savedStateHandle.get<Long>(RELEASE_ID) ?: -1

    private val _videoPlayerStateFlow: MutableStateFlow<VideoPlayerState> = MutableStateFlow(
        VideoPlayerState()
    )
    val videoPlayerStateFlow: StateFlow<VideoPlayerState> = _videoPlayerStateFlow

    companion object {
        private const val TAG = "VideoPlayerViewModel"
    }
}
