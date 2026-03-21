package com.dezdeqness.downloads.ui.episodes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.downloads.data.manager.DownloadManager
import com.dezdeqness.downloads.domain.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.domain.repository.SyncDownloadsEpisodeRepository
import com.dezdeqness.downloads.navigation.RELEASE_ID_ARG
import com.dezdeqness.downloads.ui.model.DownloadUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReleaseEpisodesState(
    val releaseTitle: String = "",
    val episodes: List<DownloadUiModel> = emptyList(),
)

class ReleaseEpisodesViewModel(
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val syncRepository: SyncDownloadsEpisodeRepository,
    private val downloadManager: DownloadManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val releaseId = savedStateHandle.get<Long>(RELEASE_ID_ARG) ?: -1L

    val state: StateFlow<ReleaseEpisodesState> = downloadEpisodeRepository
        .getCompletedByReleaseIdAsFlow(releaseId)
        .map { list ->
            val episodes = list.map { entity ->
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
                )
            }
            ReleaseEpisodesState(
                releaseTitle = episodes.firstOrNull()?.releaseTitle.orEmpty(),
                episodes = episodes,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ReleaseEpisodesState(),
        )

    fun onDeleteClicked(id: Long) {
        viewModelScope.launch {
            downloadManager.cancel(id)
            syncRepository.delete(id)
        }
    }
}
