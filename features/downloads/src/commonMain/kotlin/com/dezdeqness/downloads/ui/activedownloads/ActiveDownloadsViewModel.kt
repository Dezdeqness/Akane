package com.dezdeqness.downloads.ui.activedownloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.downloads.data.manager.DownloadManager
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.contract.repository.SyncDownloadsEpisodeRepository
import com.dezdeqness.downloads.ui.model.DownloadUiModel
import com.dezdeqness.downloads.ui.model.ReleaseGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveDownloadsState(
    val activeDownloads: List<DownloadUiModel> = emptyList(),
    val historyDownloads: List<DownloadUiModel> = emptyList(),
    val completedGroups: List<ReleaseGroup> = emptyList(),
) {
    val isEmptyState
        get() = activeDownloads.isEmpty()
                && historyDownloads.isEmpty()
                && completedGroups.isEmpty()
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
                completedGroups = uiModels
                    .filter { it.status == DownloadStatus.COMPLETED && !it.hiddenFromHistory }
                    .groupBy { it.releaseId }
                    .map { (releaseId, episodes) ->
                        val sorted = episodes.sortedBy { it.episodeOrdinal }
                        ReleaseGroup(
                            releaseId = releaseId,
                            releaseTitle = sorted.first().releaseTitle,
                            previewUrl = sorted.first().previewUrl,
                            episodes = sorted,
                            totalSize = sorted.size,
                        )
                    }
                    .sortedBy { it.releaseTitle },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ActiveDownloadsState(),
        )

    fun onDeleteClicked(id: Long) {
        viewModelScope.launch {
            downloadManager.delete(id)
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
