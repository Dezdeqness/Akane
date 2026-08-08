package com.dezdeqness.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.core.ui.views.image.LocalAstImageLoader
import com.dezdeqness.designsystem.AkaneTheme
import com.dezdeqness.designsystem.imageloader.getImageLoader
import com.dezdeqness.details.navigation.detailsEntries
import com.dezdeqness.details.navigation.navigateToDetailsScreen
import com.dezdeqness.downloads.navigation.activeDownloadsEntries
import com.dezdeqness.downloads.navigation.releaseEpisodesEntries
import com.dezdeqness.videoplayer.navigation.downloadedPlaylistEntries
import com.dezdeqness.videoplayer.navigation.videoController
import com.dezdeqness.videoplayer.navigation.videoPlayerEntries
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data object RootShellKey : NavKey

@Composable
fun App() {
    val controller = remember { videoController() }
    val analytics: AkaneAnalytics = koinInject()

    CompositionLocalProvider(
        LocalAstImageLoader provides getImageLoader()
    ) {
        AkaneTheme {
            val rootBackStack = rememberNavBackStack(navSavedStateConfiguration(), RootShellKey)

            NavDisplay(
                backStack = rootBackStack,
                onBack = { rootBackStack.removeLastOrNull() },
                modifier = Modifier.fillMaxSize(),
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    entry<RootShellKey> {
                        RootScreen(
                            rootBackStack = rootBackStack,
                            videoPlayerController = controller,
                        )
                    }
                    detailsEntries(
                        onBackPressed = { rootBackStack.removeLastOrNull() },
                        onEpisodeClick = { id, episodeId ->
                            controller.navigateToPlayer(rootBackStack, id, episodeId)
                        },
                        onReleaseClicked = { id, title ->
                            analytics.trackDetailsOpened(animeId = id, title = title)
                            rootBackStack.navigateToDetailsScreen(id)
                        }
                    )
                    videoPlayerEntries(onBackPressed = { rootBackStack.removeLastOrNull() })
                    downloadedPlaylistEntries(onBackPressed = { rootBackStack.removeLastOrNull() })
                    releaseEpisodesEntries(
                        onBackPressed = { rootBackStack.removeLastOrNull() },
                        onPlayClicked = { releaseId, episodeId ->
                            controller.navigateToDownloadedPlaylist(
                                backStack = rootBackStack,
                                releaseId = releaseId,
                                startEpisodeId = episodeId,
                            )
                        },
                    )
                    activeDownloadsEntries(onBackPressed = { rootBackStack.removeLastOrNull() })
                }
            )
        }
    }
}
