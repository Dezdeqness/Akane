package com.dezdeqness.downloads.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.data.manager.DownloadFileManager
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
    private val fileManager: DownloadFileManager,
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
                        isAvailable = fileManager.isFileAvailable(entity.filePath),
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
                        availableCount = sorted.count { it.isAvailable },
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
