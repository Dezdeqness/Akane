package com.dezdeqness.details.ui

import com.dezdeqness.screenshot.IMAGE_LOAD_ADVANCE_MS
import com.dezdeqness.screenshot.FAKE_IMAGE_URL
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.dezdeqness.details.ui.composables.ReleaseDetailsLoaded
import com.dezdeqness.details.ui.composables.ReleaseError
import com.dezdeqness.details.ui.composables.ReleaseLoading
import com.dezdeqness.details.ui.composables.ReleaseToolbarInitial
import com.dezdeqness.details.ui.model.DetailsTab
import com.dezdeqness.details.ui.model.EpisodesUiModel
import com.dezdeqness.details.ui.model.FavouriteButtonState
import com.dezdeqness.details.ui.model.FranchiseReleaseUiModel
import com.dezdeqness.details.ui.model.ReleaseDetailsHeaderUiModel
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel
import com.dezdeqness.screenshot.screenshotViewports
import kotlin.test.Test

class DetailsScreenshotTest {

    @Test
    fun loaded() = screenshotViewports("details_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS) { _ ->
        ReleaseDetailsLoaded(
            details = fakeDetails(),
            onEpisodeClick = { _, _ -> },
            onDownloadClick = {},
            onCancelDownloadClick = {},
            onDownloadAllClick = {},
            onCancelAllDownloadsClick = {},
            onBackPressed = {},
            favouriteButtonState = FavouriteButtonState.Loaded(isFavourite = true),
            onFavouriteClicked = {},
            onReleaseClicked = { _, _ -> },
        )
    }

    @Test
    fun loading() = screenshotViewports("details_loading") { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            ReleaseToolbarInitial(onBackPressed = {})
            ReleaseLoading(modifier = Modifier.fillMaxSize())
        }
    }

    @Test
    fun error() = screenshotViewports("details_error") { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            ReleaseToolbarInitial(onBackPressed = {})
            ReleaseError(modifier = Modifier.fillMaxSize(), onAction = {})
        }
    }
}

private fun fakeDetails(): ReleaseDetailsUiModel = ReleaseDetailsUiModel(
    id = 1L,
    header = ReleaseDetailsHeaderUiModel(
        id = 1L,
        title = "Cyber Blossom",
        season = "Осень",
        year = "2024",
        imageUrl = FAKE_IMAGE_URL,
    ),
    tabs = listOf(
        DetailsTab.InfoTab(
            summary = "В неоновом мегаполисе будущего хакер и андроид объединяются, чтобы " +
                "раскрыть заговор корпораций. Каждая серия приближает их к правде — и к опасности.",
            genres = listOf("Sci-Fi", "Action", "Drama"),
            type = "ТВ-сериал",
            ageRating = "16+",
            episodesTotal = 24,
            averageDuration = "24 мин",
            isOngoing = true,
        ),
        DetailsTab.EpisodesTab(
            episodes = List(8) { index ->
                EpisodesUiModel(
                    id = "ep_$index",
                    name = "Эпизод ${index + 1}",
                    previewUrl = FAKE_IMAGE_URL,
                    ordinal = (index + 1).toLong(),
                    episodeUrls = linkedMapOf("1080p" to "", "720p" to ""),
                )
            },
        ),
        DetailsTab.FranchiseTab(
            id = "franchise_1",
            name = "Cyber Blossom",
            nameEnglish = "Cyber Blossom",
            firstYear = 2019,
            lastYear = 2024,
            totalEpisodes = 48,
            totalReleases = 3,
            totalDuration = "19 ч",
            releases = List(4) { index ->
                FranchiseReleaseUiModel(
                    id = index.toLong(),
                    sortOrder = index.toLong(),
                    title = "Cyber Blossom ${index + 1}",
                    imageUrl = FAKE_IMAGE_URL,
                    year = (2019 + index).toLong(),
                    type = "ТВ",
                )
            },
        ),
        DetailsTab.StatisticsTab(
            userFavourites = 12400,
            planned = 8300,
            watched = 21500,
            watching = 3400,
            postponed = 900,
            abandoned = 600,
        ),
    ),
)
