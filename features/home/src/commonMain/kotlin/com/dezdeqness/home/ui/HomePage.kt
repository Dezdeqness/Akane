package com.dezdeqness.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dezdeqness.home.ui.composables.FreshUpdatesSection
import com.dezdeqness.home.ui.composables.HomeError
import com.dezdeqness.home.ui.composables.HomeSection
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    stateFlow: StateFlow<HomeState>,
    actions: HomeActions,
) {
    val state by stateFlow.collectAsStateWithLifecycle()

    val isLoading = state.status == StateStatus.Loading || state.status == StateStatus.Initial
    val isError = state.status == StateStatus.Error

    Box(
        modifier
            .fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (isError) {
            HomeError(
                modifier = Modifier.align(Alignment.Center),
                onAction = actions::onRetryClicked,
            )
        } else {
            LazyColumn(
                modifier = modifier,
            ) {
                item {
                    FreshUpdatesSection(
                        items = state.freshUpdates,
                        onItemClicked = actions::onItemClicked,
                    )
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
                    HomeSection(
                        title = "Лучшее за все время",
                        items = state.bestRated,
                        onItemClicked = actions::onItemClicked,
                    )
                }
            }
        }
    }
}
