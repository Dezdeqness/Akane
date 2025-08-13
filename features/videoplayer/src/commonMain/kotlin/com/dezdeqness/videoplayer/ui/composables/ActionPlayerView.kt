package com.dezdeqness.videoplayer.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.videoplayer.core.getDeviceConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionPlayerView(
    modifier: Modifier = Modifier,
    progressSlider: @Composable (Modifier) -> Unit,
    qualityAction: @Composable () -> Unit,
    speedAction: @Composable () -> Unit,
    playlistAction: @Composable () -> Unit,
) {
    val isVertical = getDeviceConfiguration().isPortrait

    if (isVertical) {
        ActionPlayerViewVertical(
            modifier = modifier,
            qualityAction = qualityAction,
            speedAction = speedAction,
            playlistAction = playlistAction,
            progressSlider = progressSlider,
        )
    } else {
        ActionPlayerViewHorizontal(
            modifier = modifier,
            progressSlider = progressSlider,
            qualityAction = qualityAction,
            speedAction = speedAction,
            playlistAction = playlistAction,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionPlayerViewVertical(
    modifier: Modifier = Modifier,
    progressSlider: @Composable (Modifier) -> Unit,
    qualityAction: @Composable () -> Unit,
    speedAction: @Composable () -> Unit,
    playlistAction: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        progressSlider(Modifier)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.weight(1f))

            qualityAction()
            speedAction()
            playlistAction()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionPlayerViewHorizontal(
    modifier: Modifier = Modifier,
    progressSlider: @Composable (Modifier) -> Unit,
    qualityAction: @Composable () -> Unit,
    speedAction: @Composable () -> Unit,
    playlistAction: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {

        progressSlider(Modifier.weight(1f))

        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            qualityAction()
            speedAction()
            playlistAction()
        }
    }
}
