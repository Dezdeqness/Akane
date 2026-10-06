package com.dezdeqness.shared

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.dezdeqness.auth.navigation.authEntries
import com.dezdeqness.details.navigation.detailsEntries
import com.dezdeqness.downloads.navigation.activeDownloadsEntries
import com.dezdeqness.downloads.navigation.downloadsEntries
import com.dezdeqness.downloads.navigation.navigateToActiveDownloads
import com.dezdeqness.downloads.navigation.navigateToReleaseEpisodes
import com.dezdeqness.downloads.navigation.releaseEpisodesEntries
import com.dezdeqness.feed.navigation.feedEntries
import com.dezdeqness.franchise.navigation.franchiseEntries
import com.dezdeqness.franchise.navigation.navigateToFranchiseDetail
import com.dezdeqness.franchise.navigation.navigateToFranchises
import com.dezdeqness.genre.navigation.genreEntries
import com.dezdeqness.genre.navigation.navigateToGenreReleases
import com.dezdeqness.genre.navigation.navigateToGenres
import com.dezdeqness.home.navigation.homeEntries
import com.dezdeqness.personal.navigation.personalEntries
import com.dezdeqness.profile.navigation.profileEntries
import com.dezdeqness.videoplayer.navigation.downloadedPlaylistEntries
import com.dezdeqness.videoplayer.navigation.navigateToDownloadedPlaylist
import com.dezdeqness.videoplayer.navigation.navigateToVideoPlayerScreen
import com.dezdeqness.videoplayer.navigation.videoPlayerEntries
import kotlinx.coroutines.flow.StateFlow

fun appEntryProvider(
    navigator: AppNavigator,
    activeDownloadsCount: StateFlow<Int>,
    stack: () -> NavBackStack<NavKey>,
    back: () -> Unit,
    openDetails: (releaseId: Long, title: String) -> Unit,
): (NavKey) -> NavEntry<NavKey> {
    return entryProvider {
        homeEntries(
            onItemClicked = openDetails,
            onContinueWatchingClicked = { releaseId, episodeId ->
                stack().navigateToDownloadedPlaylist(releaseId, episodeId)
            },
            onGenreClicked = { genreId, genreName -> stack().navigateToGenreReleases(genreId, genreName) },
            onAllGenresClicked = { stack().navigateToGenres() },
            onFranchiseClicked = { franchiseId, franchiseName ->
                stack().navigateToFranchiseDetail(franchiseId, franchiseName)
            },
            onAllFranchisesClicked = { stack().navigateToFranchises() },
        )
        genreEntries(
            onBackPressed = back,
            onGenreClicked = { genreId, genreName -> stack().navigateToGenreReleases(genreId, genreName) },
            onReleaseClicked = openDetails,
        )
        franchiseEntries(
            onBackPressed = back,
            onFranchiseClicked = { franchiseId, franchiseName ->
                stack().navigateToFranchiseDetail(franchiseId, franchiseName)
            },
            onReleaseClicked = openDetails,
        )
        feedEntries(onReleaseClicked = openDetails)
        personalEntries(
            onItemClicked = openDetails,
            onEmptyListActionClicked = { navigator.switchTab(AppTab.SEARCH) },
            onNavigateToProfile = { navigator.switchTab(AppTab.PROFILE) },
        )
        downloadsEntries(
            onReleaseClicked = { releaseId -> stack().navigateToReleaseEpisodes(releaseId) },
            activeDownloadsCountFlow = activeDownloadsCount,
            onActiveDownloadsClicked = { stack().navigateToActiveDownloads() },
        )
        profileEntries()
        authEntries(navigator.tabs.getValue(AppTab.PROFILE))
        detailsEntries(
            onBackPressed = back,
            onEpisodeClick = { releaseId, episodeId -> stack().navigateToVideoPlayerScreen(releaseId, episodeId) },
            onReleaseClicked = openDetails,
        )
        releaseEpisodesEntries(
            onBackPressed = back,
            onPlayClicked = { releaseId, episodeId -> stack().navigateToDownloadedPlaylist(releaseId, episodeId) },
        )
        activeDownloadsEntries(onBackPressed = back)
        videoPlayerEntries(onBackPressed = back)
        downloadedPlaylistEntries(onBackPressed = back)
    }
}
