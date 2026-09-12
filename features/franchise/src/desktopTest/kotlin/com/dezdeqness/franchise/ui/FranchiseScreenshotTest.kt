package com.dezdeqness.franchise.ui

import com.dezdeqness.franchise.ui.all.AllFranchisesContent
import com.dezdeqness.franchise.ui.all.AllFranchisesState
import com.dezdeqness.franchise.ui.all.AllFranchisesStatus
import com.dezdeqness.franchise.ui.detail.FranchiseDetailContent
import com.dezdeqness.franchise.ui.detail.FranchiseDetailState
import com.dezdeqness.franchise.ui.detail.FranchiseDetailStatus
import com.dezdeqness.franchise.ui.model.FranchiseHeaderUiModel
import com.dezdeqness.franchise.ui.model.FranchiseUiModel
import com.dezdeqness.screenshot.FAKE_IMAGE_URL
import com.dezdeqness.screenshot.IMAGE_LOAD_ADVANCE_MS
import com.dezdeqness.screenshot.fakeReleaseList
import com.dezdeqness.screenshot.screenshotViewports
import kotlin.test.Test

class FranchiseScreenshotTest {

    @Test
    fun allLoaded() = screenshotViewports("all_franchises_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS) { _ ->
        AllFranchisesContent(
            state = AllFranchisesState(
                franchises = fakeFranchiseList(),
                status = AllFranchisesStatus.Loaded,
            ),
            onBackPressed = {},
            onRetryClicked = {},
            onFranchiseClicked = { _, _ -> },
        )
    }

    @Test
    fun allLoading() = screenshotViewports("all_franchises_loading") { _ ->
        AllFranchisesContent(
            state = AllFranchisesState(status = AllFranchisesStatus.Loading),
            onBackPressed = {},
            onRetryClicked = {},
            onFranchiseClicked = { _, _ -> },
        )
    }

    @Test
    fun allError() = screenshotViewports("all_franchises_error") { _ ->
        AllFranchisesContent(
            state = AllFranchisesState(status = AllFranchisesStatus.Error),
            onBackPressed = {},
            onRetryClicked = {},
            onFranchiseClicked = { _, _ -> },
        )
    }

    @Test
    fun detailLoaded() = screenshotViewports("franchise_detail_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS) { _ ->
        FranchiseDetailContent(
            state = FranchiseDetailState(
                header = FranchiseHeaderUiModel(
                    name = "Cyber Blossom",
                    imageUrl = FAKE_IMAGE_URL,
                    meta = listOf("12 releases", "2019–2024", "TV / Movie"),
                ),
                items = fakeReleaseList(),
                status = FranchiseDetailStatus.Loaded,
            ),
            franchiseName = "Cyber Blossom",
            onBackPressed = {},
            onReleaseClicked = { _, _ -> },
            onRetryClicked = {},
        )
    }

    @Test
    fun detailLoading() = screenshotViewports("franchise_detail_loading") { _ ->
        FranchiseDetailContent(
            state = FranchiseDetailState(status = FranchiseDetailStatus.Loading),
            franchiseName = "Cyber Blossom",
            onBackPressed = {},
            onReleaseClicked = { _, _ -> },
            onRetryClicked = {},
        )
    }

    @Test
    fun detailError() = screenshotViewports("franchise_detail_error") { _ ->
        FranchiseDetailContent(
            state = FranchiseDetailState(status = FranchiseDetailStatus.Error),
            franchiseName = "Cyber Blossom",
            onBackPressed = {},
            onReleaseClicked = { _, _ -> },
            onRetryClicked = {},
        )
    }
}

private fun fakeFranchiseList(count: Int = 12): List<FranchiseUiModel> = List(count) { index ->
    FranchiseUiModel(
        id = "franchise_$index",
        name = "Franchise $index",
        imageUrl = FAKE_IMAGE_URL,
        totalReleases = (index + 1) * 3,
    )
}
