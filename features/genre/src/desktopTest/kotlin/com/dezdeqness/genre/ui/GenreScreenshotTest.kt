package com.dezdeqness.genre.ui

import com.dezdeqness.genre.ui.all.AllGenresContent
import com.dezdeqness.genre.ui.all.AllGenresState
import com.dezdeqness.genre.ui.all.AllGenresStatus
import com.dezdeqness.genre.ui.releases.GenreReleasesContent
import com.dezdeqness.genre.ui.releases.GenreReleasesState
import com.dezdeqness.genre.ui.releases.GenreReleasesStatus
import com.dezdeqness.screenshot.IMAGE_LOAD_ADVANCE_MS
import com.dezdeqness.screenshot.fakeReleaseList
import com.dezdeqness.screenshot.screenshotViewports
import kotlin.test.Test

class GenreScreenshotTest {

    @Test
    fun releasesLoaded() = screenshotViewports("genre_releases_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS) { _ ->
        GenreReleasesContent(
            state = GenreReleasesState(
                items = fakeReleaseList(),
                status = GenreReleasesStatus.Loaded,
                hasNextPage = false,
            ),
            genreName = "Sci-Fi",
            onBackPressed = {},
            onRetryClicked = {},
            onLoadMore = {},
            onReleaseClicked = { _, _ -> },
        )
    }

    @Test
    fun releasesLoading() = screenshotViewports("genre_releases_loading") { _ ->
        GenreReleasesContent(
            state = GenreReleasesState(status = GenreReleasesStatus.Loading),
            genreName = "Sci-Fi",
            onBackPressed = {},
            onRetryClicked = {},
            onLoadMore = {},
            onReleaseClicked = { _, _ -> },
        )
    }

    @Test
    fun releasesError() = screenshotViewports("genre_releases_error") { _ ->
        GenreReleasesContent(
            state = GenreReleasesState(status = GenreReleasesStatus.Error),
            genreName = "Sci-Fi",
            onBackPressed = {},
            onRetryClicked = {},
            onLoadMore = {},
            onReleaseClicked = { _, _ -> },
        )
    }

    @Test
    fun releasesEmpty() = screenshotViewports("genre_releases_empty") { _ ->
        GenreReleasesContent(
            state = GenreReleasesState(status = GenreReleasesStatus.Empty),
            genreName = "Sci-Fi",
            onBackPressed = {},
            onRetryClicked = {},
            onLoadMore = {},
            onReleaseClicked = { _, _ -> },
        )
    }

    @Test
    fun allGenresLoaded() = screenshotViewports("all_genres_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS) { _ ->
        AllGenresContent(
            state = AllGenresState(
                genres = fakeGenreList(),
                status = AllGenresStatus.Loaded,
            ),
            onBackPressed = {},
            onRetryClicked = {},
            onGenreClicked = { _, _ -> },
        )
    }

    @Test
    fun allGenresLoading() = screenshotViewports("all_genres_loading") { _ ->
        AllGenresContent(
            state = AllGenresState(status = AllGenresStatus.Loading),
            onBackPressed = {},
            onRetryClicked = {},
            onGenreClicked = { _, _ -> },
        )
    }

    @Test
    fun allGenresError() = screenshotViewports("all_genres_error") { _ ->
        AllGenresContent(
            state = AllGenresState(status = AllGenresStatus.Error),
            onBackPressed = {},
            onRetryClicked = {},
            onGenreClicked = { _, _ -> },
        )
    }
}
