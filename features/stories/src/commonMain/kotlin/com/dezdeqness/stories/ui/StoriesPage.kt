package com.dezdeqness.stories.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.stories.ui.composables.PlaybackLifecycleEffect
import com.dezdeqness.stories.ui.composables.StoriesMessage
import com.dezdeqness.stories.ui.composables.StoriesPager
import com.dezdeqness.stories.ui.favourite.StoryFavouriteViewModel

@Composable
fun StoriesPage(
    viewModel: StoriesViewModel,
    favouriteViewModel: StoryFavouriteViewModel,
    onClose: () -> Unit,
    onEpisodeClicked: (releaseId: Long, episodeId: String) -> Unit,
    onDetailsClicked: (releaseId: Long, title: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    PlaybackLifecycleEffect(onResumedChanged = viewModel::onResumedChanged)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        when (state.status) {
            StoriesState.Status.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
            )

            StoriesState.Status.Error -> StoriesMessage(
                modifier = Modifier.align(Alignment.Center),
                text = "Не удалось загрузить истории",
                buttonText = "Повторить",
                onClick = viewModel::retry,
            )

            StoriesState.Status.Empty -> StoriesMessage(
                modifier = Modifier.align(Alignment.Center),
                text = "На этой неделе новых серий пока нет",
                buttonText = "Закрыть",
                onClick = onClose,
            )

            StoriesState.Status.Loaded -> StoriesPager(
                state = state,
                viewModel = viewModel,
                favouriteViewModel = favouriteViewModel,
                onClose = onClose,
                onEpisodeClicked = { story -> onEpisodeClicked(story.releaseId, story.episodeId) },
                onDetailsClicked = { story -> onDetailsClicked(story.releaseId, story.releaseName) },
            )
        }
    }
}
