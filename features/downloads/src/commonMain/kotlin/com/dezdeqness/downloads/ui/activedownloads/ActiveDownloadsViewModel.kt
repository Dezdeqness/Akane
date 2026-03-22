package com.dezdeqness.downloads.ui.activedownloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.downloads.data.manager.DownloadManager
import com.dezdeqness.downloads.domain.model.DownloadStatus
import com.dezdeqness.downloads.domain.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.domain.repository.SyncDownloadsEpisodeRepository
import com.dezdeqness.downloads.ui.model.DownloadUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveDownloadsState(
    val activeDownloads: List<DownloadUiModel> = emptyList(),
    val historyDownloads: List<DownloadUiModel> = emptyList(),
    val completedDownloads: List<DownloadUiModel> = emptyList(),
) {
    val isEmptyState
        get() = activeDownloads.isEmpty()
                && historyDownloads.isEmpty()
                && completedDownloads.isEmpty()
}

class ActiveDownloadsViewModel(
    downloadEpisodeRepository: DownloadEpisodeRepository,
    private val syncRepository: SyncDownloadsEpisodeRepository,
    private val downloadManager: DownloadManager,
) : ViewModel() {

    val state: StateFlow<ActiveDownloadsState> = downloadEpisodeRepository
        .getAllDownloadsAsFlow()
        .map { list ->
            val uiModels = list.map { entity ->
                DownloadUiModel(
                    id = entity.id,
                    releaseId = entity.releaseId,
                    episodeId = entity.episodeId,
                    episodeName = entity.episodeName,
                    episodeOrdinal = entity.episodeOrdinal,
                    releaseTitle = entity.releaseTitle,
                    quality = entity.quality,
                    progress = entity.progress,
                    status = entity.status,
                    previewUrl = entity.previewUrl,
                    filePath = entity.filePath,
                    hiddenFromHistory = entity.hiddenFromHistory,
                )
            }

            val activeStatuses = setOf(
                DownloadStatus.DOWNLOADING,
                DownloadStatus.QUEUED,
                DownloadStatus.REMUXING,
                DownloadStatus.PAUSED,
            )

            val historyStatuses = setOf(
                DownloadStatus.FAILED,
                DownloadStatus.CANCELLED,
            )

            ActiveDownloadsState(
                activeDownloads = uiModels.filter { it.status in activeStatuses }
                    .sortedBy { it.episodeOrdinal },
                historyDownloads = uiModels.filter { it.status in historyStatuses }
                    .sortedBy { it.episodeOrdinal },
                completedDownloads = uiModels
                    .filter { it.status == DownloadStatus.COMPLETED && !it.hiddenFromHistory }
                    .sortedBy { it.episodeOrdinal },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ActiveDownloadsState(),
        )

    fun onDeleteClicked(id: Long) {
        viewModelScope.launch {
            downloadManager.cancel(id)
            syncRepository.delete(id)
        }
    }

    fun onRetryClicked(id: Long) {
        viewModelScope.launch {
            syncRepository.updateStatus(id, DownloadStatus.QUEUED)
            downloadManager.enqueue(id)
        }
    }

    fun onCancelClicked(id: Long) {
        viewModelScope.launch {
            downloadManager.cancel(id)
            syncRepository.updateStatus(id, DownloadStatus.CANCELLED)
        }
    }

    fun onHideFromHistoryClicked(id: Long) {
        viewModelScope.launch {
            syncRepository.hideFromHistory(id)
        }
    }

    fun onPauseClicked(id: Long) {
        viewModelScope.launch {
            downloadManager.pause(id)
            syncRepository.updateStatus(id, DownloadStatus.PAUSED)
        }
    }
}
