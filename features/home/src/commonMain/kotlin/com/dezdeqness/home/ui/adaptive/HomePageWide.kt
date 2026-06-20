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
import com.dezdeqness.home.ui.composables.HomeWideSection
import com.dezdeqness.home.ui.composables.freshupdates.FreshUpdatesWideSection
import com.dezdeqness.home.ui.composables.franchise.FranchisesWideSection
import com.dezdeqness.home.ui.composables.genres.GenresWideSection
import com.dezdeqness.home.ui.composables.promo.PromoWideSection

@Composable
fun HomePageWide(
    state: HomeState,
    actions: HomeActions,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 28.dp),
    ) {
        item {
            PromoWideSection(
                promos = state.promos,
                onReleaseClicked = actions::onPromoReleaseClicked,
            )
        }
        item {
            FreshUpdatesWideSection(
                items = state.freshUpdates,
                onItemClicked = actions::onItemClicked,
            )
        }
        item {
            HomeWideSection(
                title = "Новинки",
                items = state.onGoing,
                onItemClicked = actions::onItemClicked,
            )
        }
        item {
            HomeWideSection(
                title = "Вышедшее",
                items = state.released,
                onItemClicked = actions::onItemClicked,
            )
        }
        item {
            FranchisesWideSection(
                franchises = state.franchises,
                onFranchiseClicked = actions::onFranchiseClicked,
                onAllClicked = actions::onAllFranchisesClicked,
            )
        }
        item {
            HomeWideSection(
                title = "Лучшее за все время",
                items = state.bestRated,
                onItemClicked = actions::onItemClicked,
            )
        }
        item {
            GenresWideSection(
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
