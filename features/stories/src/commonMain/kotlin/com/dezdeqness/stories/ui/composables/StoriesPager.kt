package com.dezdeqness.stories.ui.composables

import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.foundation.utils.collectEvents
import com.dezdeqness.stories.ui.StoriesEvent
import com.dezdeqness.stories.ui.StoriesState
import com.dezdeqness.stories.ui.StoriesViewModel
import com.dezdeqness.stories.ui.favourite.StoryFavouriteViewModel
import com.dezdeqness.stories.ui.model.StoryUiModel
import com.dezdeqness.videoplayer.core.player.compositedVideoSurfaceFollowsTransforms

@Composable
internal fun StoriesPager(
    state: StoriesState,
    viewModel: StoriesViewModel,
    favouriteViewModel: StoryFavouriteViewModel,
    onClose: () -> Unit,
    onEpisodeClicked: (StoryUiModel) -> Unit,
    onDetailsClicked: (StoryUiModel) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = state.currentCollection) { state.collections.size }
    val playback by viewModel.playbackState.collectAsStateOnLifecycle()
    val players by viewModel.players.collectAsStateOnLifecycle()
    val isMuted by viewModel.isMuted.collectAsStateOnLifecycle()
    val favourite by favouriteViewModel.state.collectAsStateOnLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect(viewModel::onCollectionSettled)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }.collect(viewModel::onTransitionChanged)
    }
    LaunchedEffect(state.currentCollection) {
        if (!pagerState.isScrollInProgress && pagerState.settledPage != state.currentCollection) {
            pagerState.scrollToPage(state.currentCollection)
        }
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    viewModel.events.collectEvents { event ->
        when (event) {
            is StoriesEvent.ScrollToCollection -> pagerState.animateScrollToPage(
                page = event.index,
                animationSpec = tween(durationMillis = CUBE_ANIMATION_MS),
            )

            StoriesEvent.Close -> onClose()
        }
    }

    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        key = { page -> state.collections[page].id },
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                when (event.key) {
                    Key.DirectionRight -> viewModel.onNext().let { true }
                    Key.DirectionLeft -> viewModel.onPrevious().let { true }
                    Key.Escape -> onClose().let { true }
                    else -> false
                }
            },
    ) { page ->
        val isSettled = page == pagerState.settledPage
        val isActive = isSettled && !pagerState.isScrollInProgress
        val collection = state.collections[page]
        val player = collection.stories.getOrNull(state.storyIndexOf(page))?.let { players[it.episodeId] }

        val showVideo = if (compositedVideoSurfaceFollowsTransforms) true else isActive

        StoryCollectionPage(
            modifier = Modifier.cubeTransition(pagerState, page),
            collection = collection,
            storyIndex = state.storyIndexOf(page),
            isActive = isActive,
            showVideo = showVideo,
            player = player,
            playback = playback,
            isMuted = isMuted,
            favourite = favourite,
            onPrevious = viewModel::onPrevious,
            onNext = viewModel::onNext,
            onHoldChanged = viewModel::onHoldChanged,
            onMuteClicked = viewModel::onMuteClicked,
            onFavouriteClicked = { story -> favouriteViewModel.onFavouriteClicked(story.releaseId) },
            onClose = onClose,
            onEpisodeClicked = onEpisodeClicked,
            onDetailsClicked = onDetailsClicked,
        )
    }
}

private const val CUBE_ANIMATION_MS = 450
