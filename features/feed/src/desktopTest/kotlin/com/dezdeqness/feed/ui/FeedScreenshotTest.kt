package com.dezdeqness.feed.ui

import com.dezdeqness.screenshot.DefaultShots
import com.dezdeqness.screenshot.IMAGE_LOAD_ADVANCE_MS
import com.dezdeqness.screenshot.Viewport
import com.dezdeqness.screenshot.fakeReleaseList
import com.dezdeqness.screenshot.screenshotViewports
import kotlin.test.Test

class FeedScreenshotTest {

    private val shots = DefaultShots.filterNot { it.viewport == Viewport.Desktop }

    @Test
    fun loaded() = screenshotViewports("feed_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS, shots = shots) { _ ->
        FeedScreen(
            state = FeedState(
                items = fakeReleaseList(),
                status = Status.Loaded,
                hasNextPage = false,
            ),
            isFilterVisible = false,
            onQueryChanged = {},
            onFilterClicked = {},
            onRetryClicked = {},
            onFilterClosed = {},
            onFilterChanged = {},
            onLoadMore = {},
            onReleaseClicked = { _, _ -> },
        )
    }

    @Test
    fun loading() = screenshotViewports("feed_loading", shots = shots) { _ ->
        FeedScreen(
            state = FeedState(status = Status.Loading),
            isFilterVisible = false,
            onQueryChanged = {},
            onFilterClicked = {},
            onRetryClicked = {},
            onFilterClosed = {},
            onFilterChanged = {},
            onLoadMore = {},
            onReleaseClicked = { _, _ -> },
        )
    }

    @Test
    fun error() = screenshotViewports("feed_error", shots = shots) { _ ->
        FeedScreen(
            state = FeedState(status = Status.Error),
            isFilterVisible = false,
            onQueryChanged = {},
            onFilterClicked = {},
            onRetryClicked = {},
            onFilterClosed = {},
            onFilterChanged = {},
            onLoadMore = {},
            onReleaseClicked = { _, _ -> },
        )
    }

    @Test
    fun empty() = screenshotViewports("feed_empty", shots = shots) { _ ->
        FeedScreen(
            state = FeedState(status = Status.Empty),
            isFilterVisible = false,
            onQueryChanged = {},
            onFilterClicked = {},
            onRetryClicked = {},
            onFilterClosed = {},
            onFilterChanged = {},
            onLoadMore = {},
            onReleaseClicked = { _, _ -> },
        )
    }
}
