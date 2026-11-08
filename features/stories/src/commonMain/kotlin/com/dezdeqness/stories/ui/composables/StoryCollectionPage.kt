package com.dezdeqness.stories.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dezdeqness.stories.ui.favourite.FavouriteState
import com.dezdeqness.stories.ui.model.StoryCollectionUiModel
import com.dezdeqness.stories.ui.model.StoryUiModel
import com.dezdeqness.stories.ui.playback.StoryPlayback
import com.dezdeqness.videoplayer.core.player.VideoPlayerManager

@Composable
fun StoryCollectionPage(
    collection: StoryCollectionUiModel,
    storyIndex: Int,
    isActive: Boolean,
    showVideo: Boolean,
    player: VideoPlayerManager?,
    playback: StoryPlayback,
    isMuted: Boolean,
    favourite: FavouriteState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onHoldChanged: (Boolean) -> Unit,
    onMuteClicked: () -> Unit,
    onFavouriteClicked: (StoryUiModel) -> Unit,
    onClose: () -> Unit,
    onEpisodeClicked: (StoryUiModel) -> Unit,
    onDetailsClicked: (StoryUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val story = collection.stories.getOrNull(storyIndex) ?: return

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(PAGE_BACKGROUND),
    ) {
        StoryVideo(
            modifier = Modifier.align(Alignment.Center),
            story = story,
            player = player.takeIf { showVideo },
            onClick = { onEpisodeClicked(story) },
        )

        StoryTapZones(
            onPrevious = onPrevious,
            onNext = onNext,
            onHoldChanged = onHoldChanged,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StoryProgressBar(
                count = collection.stories.size,
                currentIndex = storyIndex,
                currentProgress = if (isActive && playback.episodeId == story.episodeId) {
                    rememberSmoothLoopProgress(playback)
                } else {
                    0f
                },
            )
            StoryHeader(
                title = collection.title,
                story = IndexedStory(storyIndex, story),
                isMuted = isMuted,
                onMuteClicked = onMuteClicked,
                onClose = onClose,
            )
        }

        StoryCtaBanner(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            story = IndexedStory(storyIndex, story),
            favourite = favourite,
            onFavouriteClicked = { onFavouriteClicked(story) },
            onClick = { onDetailsClicked(story) },
        )
    }
}

private val PAGE_BACKGROUND = Brush.verticalGradient(
    0f to Color(0xFF26262A),
    0.3f to Color.Black,
    0.7f to Color.Black,
    1f to Color(0xFF2C2C30),
)
