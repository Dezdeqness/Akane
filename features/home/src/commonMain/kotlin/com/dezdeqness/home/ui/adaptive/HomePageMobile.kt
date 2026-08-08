package com.dezdeqness.home.ui.adaptive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.home.ui.HomeActions
import com.dezdeqness.home.ui.HomeState
import com.dezdeqness.home.ui.composables.HomeSecondPartFooter
import com.dezdeqness.home.ui.composables.HomeSection
import com.dezdeqness.home.ui.composables.continuewatching.ContinueWatchingSection
import com.dezdeqness.home.ui.composables.freshupdates.FreshUpdatesSection
import com.dezdeqness.home.ui.composables.franchise.FranchisesSection
import com.dezdeqness.home.ui.composables.genres.GenresSection
import com.dezdeqness.home.ui.composables.promo.PromoSection

@Composable
fun HomePageMobile(
    state: HomeState,
    actions: HomeActions,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        item {
            PromoSection(
                promos = state.promos,
                onReleaseClicked = actions::onPromoReleaseClicked,
            )
        }
        item {
            FreshUpdatesSection(
                items = state.freshUpdates,
                onItemClicked = actions::onItemClicked,
            )
        }
        if (state.continueWatching != null) {
            item {
                ContinueWatchingSection(
                    item = state.continueWatching,
                    onItemClicked = actions::onContinueWatchingClicked,
                )
            }
        }
        item {
            HomeSection(
                title = "Новинки",
                items = state.onGoing,
                onItemClicked = actions::onItemClicked,
            )
        }
        item {
            HomeSection(
                title = "Вышедшее",
                items = state.released,
                onItemClicked = actions::onItemClicked,
            )
        }
        item {
            FranchisesSection(
                franchises = state.franchises,
                onFranchiseClicked = actions::onFranchiseClicked,
                onAllClicked = actions::onAllFranchisesClicked,
            )
        }
        item {
            HomeSection(
                title = "Лучшее за все время",
                items = state.bestRated,
                onItemClicked = actions::onItemClicked,
            )
        }
        item {
            GenresSection(
                genres = state.genres,
                onGenreClicked = actions::onGenreClicked,
                onAllClicked = actions::onAllGenresClicked,
            )
        }
        item {
            HomeSecondPartFooter(
                status = state.status,
                onRetry = actions::onRetryClicked,
            )
        }
    }
}
