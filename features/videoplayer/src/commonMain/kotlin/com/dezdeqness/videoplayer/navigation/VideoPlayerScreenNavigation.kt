package com.dezdeqness.videoplayer.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.videoplayer.ui.VideoPlayerPage
import com.dezdeqness.videoplayer.ui.VideoPlayerViewModel
import io.ktor.http.encodeURLParameter
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class VideoPlayerRoute(val id: Long, val episodeId: String) : NavKey

@Serializable
data class DownloadedPlaylistRoute(val downloadReleaseId: Long, val downloadStartEpisodeId: String) : NavKey

fun EntryProviderScope<NavKey>.videoPlayerEntries(onBackPressed: () -> Unit) {
    entry<VideoPlayerRoute> { key ->
        val viewModel: VideoPlayerViewModel = koinViewModel {
            parametersOf(key.id, key.episodeId, -1L, "")
        }
        VideoPlayerPage(viewModel = viewModel, onBackPressed = onBackPressed)
    }
}

fun EntryProviderScope<NavKey>.downloadedPlaylistEntries(onBackPressed: () -> Unit) {
    entry<DownloadedPlaylistRoute> { key ->
        val viewModel: VideoPlayerViewModel = koinViewModel {
            parametersOf(-1L, "", key.downloadReleaseId, key.downloadStartEpisodeId)
        }
        VideoPlayerPage(viewModel = viewModel, onBackPressed = onBackPressed)
    }
}

fun NavBackStack<NavKey>.navigateToVideoPlayerScreen(id: Long, episodeId: String) {
    add(VideoPlayerRoute(id, episodeId))
}

fun NavBackStack<NavKey>.navigateToDownloadedPlaylist(releaseId: Long, startEpisodeId: String) {
    add(DownloadedPlaylistRoute(downloadReleaseId = releaseId, downloadStartEpisodeId = startEpisodeId.encodeURLParameter()))
}

class VideoPlayerNavigationControllerImpl : VideoPlayerNavigationController {
    override fun navigateToPlayer(backStack: NavBackStack<NavKey>, id: Long, episodeId: String) {
        backStack.navigateToVideoPlayerScreen(id = id, episodeId = episodeId)
    }

    override fun navigateToDownloadedPlaylist(backStack: NavBackStack<NavKey>, releaseId: Long, startEpisodeId: String) {
        backStack.navigateToDownloadedPlaylist(releaseId = releaseId, startEpisodeId = startEpisodeId)
    }
}
