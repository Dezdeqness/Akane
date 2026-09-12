package com.dezdeqness.downloads.ui

import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.ui.activedownloads.ActiveDownloadsContent
import com.dezdeqness.downloads.ui.activedownloads.ActiveDownloadsState
import com.dezdeqness.downloads.ui.episodes.ReleaseEpisodesContent
import com.dezdeqness.downloads.ui.episodes.ReleaseEpisodesState
import com.dezdeqness.downloads.ui.library.LibraryContent
import com.dezdeqness.downloads.ui.library.LibraryState
import com.dezdeqness.downloads.ui.model.DownloadUiModel
import com.dezdeqness.downloads.ui.model.ReleaseGroup
import com.dezdeqness.screenshot.FAKE_IMAGE_URL
import com.dezdeqness.screenshot.IMAGE_LOAD_ADVANCE_MS
import com.dezdeqness.screenshot.screenshotViewports
import kotlin.test.Test

class DownloadsScreenshotTest {

    @Test
    fun activeLoaded() = screenshotViewports("downloads_active_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS) { _ ->
        ActiveDownloadsContent(
            state = ActiveDownloadsState(
                activeDownloads = listOf(
                    fakeDownload(0, DownloadStatus.DOWNLOADING, progress = 0.42f),
                    fakeDownload(1, DownloadStatus.QUEUED, progress = 0f),
                    fakeDownload(2, DownloadStatus.PAUSED, progress = 0.15f),
                ),
                historyDownloads = listOf(
                    fakeDownload(3, DownloadStatus.FAILED, progress = 0.6f),
                ),
                completedGroups = listOf(fakeGroup(10, episodes = 6)),
            ),
            onBackPressed = {},
            onDeleteClicked = {},
            onRetryClicked = {},
            onCancelClicked = {},
            onPauseClicked = {},
            onHideFromHistoryClicked = {},
        )
    }

    @Test
    fun activeEmpty() = screenshotViewports("downloads_active_empty") { _ ->
        ActiveDownloadsContent(
            state = ActiveDownloadsState(),
            onBackPressed = {},
            onDeleteClicked = {},
            onRetryClicked = {},
            onCancelClicked = {},
            onPauseClicked = {},
            onHideFromHistoryClicked = {},
        )
    }

    @Test
    fun libraryLoaded() = screenshotViewports("downloads_library_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS) { _ ->
        LibraryContent(
            state = LibraryState(
                library = List(8) { fakeGroup(it.toLong(), episodes = (it % 5) + 1) },
            ),
            activeDownloadsCount = 3,
            onReleaseClicked = {},
            onActiveDownloadsClicked = {},
        )
    }

    @Test
    fun libraryEmpty() = screenshotViewports("downloads_library_empty") { _ ->
        LibraryContent(
            state = LibraryState(),
            activeDownloadsCount = 0,
            onReleaseClicked = {},
            onActiveDownloadsClicked = {},
        )
    }

    @Test
    fun episodesLoaded() = screenshotViewports("downloads_episodes_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS) { _ ->
        ReleaseEpisodesContent(
            state = ReleaseEpisodesState(
                releaseTitle = "Cyber Blossom",
                episodes = List(6) { fakeDownload(it.toLong(), DownloadStatus.COMPLETED, progress = 1f) },
            ),
            onBackPressed = {},
            onPlayClicked = { _, _ -> },
            onDeleteClicked = {},
        )
    }

    @Test
    fun episodesEmpty() = screenshotViewports("downloads_episodes_empty") { _ ->
        ReleaseEpisodesContent(
            state = ReleaseEpisodesState(releaseTitle = "Cyber Blossom", episodes = emptyList()),
            onBackPressed = {},
            onPlayClicked = { _, _ -> },
            onDeleteClicked = {},
        )
    }
}

private fun fakeDownload(
    id: Long,
    status: DownloadStatus,
    progress: Float,
): DownloadUiModel = DownloadUiModel(
    id = id,
    releaseId = id / 10,
    episodeId = "ep_$id",
    episodeName = "Episode ${id + 1}",
    episodeOrdinal = id + 1,
    releaseTitle = "Cyber Blossom",
    quality = "1080p",
    progress = progress,
    status = status,
    previewUrl = FAKE_IMAGE_URL,
    filePath = if (status == DownloadStatus.COMPLETED) "/tmp/ep_$id.mp4" else null,
    isAvailable = true,
)

private fun fakeGroup(releaseId: Long, episodes: Int): ReleaseGroup = ReleaseGroup(
    releaseId = releaseId,
    releaseTitle = "Release $releaseId",
    previewUrl = FAKE_IMAGE_URL,
    episodes = List(episodes) { fakeDownload(releaseId * 100 + it, DownloadStatus.COMPLETED, 1f) },
    totalSize = episodes,
    availableCount = episodes,
)
