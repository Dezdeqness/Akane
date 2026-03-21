package com.dezdeqness.downloads.ui.episodes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.downloads.ui.model.DownloadUiModel

@Composable
internal fun EpisodesList(
    episodes: List<DownloadUiModel>,
    onPlayClicked: (releaseId: Long, episodeId: String) -> Unit,
    onDeleteClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(
            items = episodes,
            key = { it.id },
        ) { episode ->
            EpisodeDownloadItem(
                episode = episode,
                onPlayClicked = {
                    onPlayClicked(episode.releaseId, episode.episodeId)
                },
                onDeleteClicked = {
                    onDeleteClicked(episode.id)
                },
            )
        }
    }
}
