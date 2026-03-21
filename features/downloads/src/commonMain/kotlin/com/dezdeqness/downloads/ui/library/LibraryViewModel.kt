package com.dezdeqness.downloads.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.downloads.domain.model.DownloadStatus
import com.dezdeqness.downloads.domain.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.ui.model.DownloadUiModel
import com.dezdeqness.downloads.ui.model.ReleaseGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class LibraryState(
    val library: List<ReleaseGroup> = emptyList(),
)

class LibraryViewModel(
    downloadEpisodeRepository: DownloadEpisodeRepository,
) : ViewModel() {

    val state: StateFlow<LibraryState> = downloadEpisodeRepository
        .getAllDownloadsAsFlow()
        .map { list ->
            val completed = list
                .filter { it.status == DownloadStatus.COMPLETED }
                .map { entity ->
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

            val library = completed
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
                .sortedBy { it.releaseTitle }

            LibraryState(library = library)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = LibraryState(),
        )
}
