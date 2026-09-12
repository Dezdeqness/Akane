package com.dezdeqness.home.ui

import com.dezdeqness.screenshot.FAKE_IMAGE_URL
import com.dezdeqness.home.ui.model.ContinueWatchingUiModel
import com.dezdeqness.home.ui.model.FranchisePanelUiModel
import com.dezdeqness.home.ui.model.GenrePanelUiModel
import com.dezdeqness.home.ui.model.HomeUiModel
import com.dezdeqness.home.ui.model.PromoPanelUiModel
import com.dezdeqness.promo.contract.model.PromoTarget

fun fakeHomeState(status: StateStatus = StateStatus.Loaded): HomeState = HomeState(
    promos = List(3) { index ->
        PromoPanelUiModel(
            id = "promo_$index",
            imageUrl = FAKE_IMAGE_URL,
            title = "Featured release #$index",
            actionLabel = "Watch now",
            isAd = index == 0,
            hasOverlay = true,
            target = PromoTarget.None,
        )
    },
    freshUpdates = fakeHomeItems(),
    continueWatching = ContinueWatchingUiModel(
        releaseId = 1L,
        episodeId = "ep_3",
        title = "Cyber Blossom",
        episodeTitle = "Episode 3",
        imagePath = FAKE_IMAGE_URL,
    ),
    onGoing = fakeHomeItems(),
    franchises = List(4) { index ->
        FranchisePanelUiModel(
            id = "franchise_$index",
            name = "Franchise $index",
            imageUrl = FAKE_IMAGE_URL,
            totalReleases = index + 2,
        )
    },
    released = fakeHomeItems(),
    bestRated = fakeHomeItems(),
    genres = List(6) { index ->
        GenrePanelUiModel(
            id = index,
            name = "Genre $index",
            imageUrl = FAKE_IMAGE_URL,
            totalReleases = (index + 1) * 7,
        )
    },
    status = status,
)

private fun fakeHomeItems(count: Int = 8): List<HomeUiModel> = List(count) { index ->
    HomeUiModel(
        id = index.toLong(),
        name = "Release $index",
        imagePath = FAKE_IMAGE_URL,
        description = "Short description for release $index.",
    )
}

object NoOpHomeActions : HomeActions {
    override fun onItemClicked(details: HomeUiModel) = Unit
    override fun onContinueWatchingClicked(item: ContinueWatchingUiModel) = Unit
    override fun onGenreClicked(genre: GenrePanelUiModel) = Unit
    override fun onAllGenresClicked() = Unit
    override fun onPromoReleaseClicked(releaseId: Long, title: String) = Unit
    override fun onFranchiseClicked(franchise: FranchisePanelUiModel) = Unit
    override fun onAllFranchisesClicked() = Unit
    override fun onRetryClicked() = Unit
}
