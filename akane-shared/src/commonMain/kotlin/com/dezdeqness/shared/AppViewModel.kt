package com.dezdeqness.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.downloads.data.manager.DownloadManager
import com.dezdeqness.downloads.domain.repository.DownloadEpisodeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
    downloadEpisodeRepository: DownloadEpisodeRepository,
    private val downloadManager: DownloadManager,
) : ViewModel() {

    val activeDownloadsCount: StateFlow<Int> =
        downloadEpisodeRepository.getActiveDownloadsCountAsFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    init {
        viewModelScope.launch {
            downloadManager.recoverStaleDownloads()
        }
    }
}
