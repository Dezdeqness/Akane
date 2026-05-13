package com.dezdeqness.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.auth.contract.session.SessionManager
import com.dezdeqness.auth.contract.session.SessionState
import com.dezdeqness.downloads.data.manager.DownloadManager
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
    downloadEpisodeRepository: DownloadEpisodeRepository,
    private val downloadManager: DownloadManager,
    private val sessionManager: SessionManager,
) : ViewModel() {

    val activeDownloadsCount: StateFlow<Int> =
        downloadEpisodeRepository.getActiveDownloadsCountAsFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val sessionState: StateFlow<SessionState> = sessionManager.sessionState

    init {
        viewModelScope.launch {
            downloadManager.recoverStaleDownloads()
        }
        viewModelScope.launch {
            sessionManager.restoreSession()
        }
    }
}
