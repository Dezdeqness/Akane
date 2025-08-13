package com.dezdeqness.videoplayer.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.dezdeqness.videoplayer.core.getDeviceConfiguration

private val standardEnter = fadeIn(
    tween(
        durationMillis = 250,
        delayMillis = 0,
        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f),
    )
)

private val standardExit = fadeOut(
    tween(
        durationMillis = 200,
        delayMillis = 0,
        easing = CubicBezierEasing(0.3f, 0.0f, 1f, 1f),
    )
)

@Composable
fun VideoLayout(
    modifier: Modifier = Modifier,
    isSystemBarVisible: Boolean,
    appbar: @Composable () -> Unit,
    videoPlayerView: @Composable (Modifier) -> Unit,
    actionPlayerView: @Composable () -> Unit,
    controlPlayerView: @Composable () -> Unit,
) {
    val safePadding = WindowInsets.safeDrawing.asPaddingValues()

    val isPortrait = getDeviceConfiguration().isPortrait

    val padding = if (isPortrait) {
        PaddingValues(
            top = safePadding.calculateTopPadding(),
            bottom = safePadding.calculateBottomPadding(),
        )
    } else {
        PaddingValues(
            start = safePadding.calculateLeftPadding(LayoutDirection.Ltr),
            end = safePadding.calculateRightPadding(LayoutDirection.Ltr),
        )
    }

    Box(modifier = modifier) {

        videoPlayerView(Modifier.align(Alignment.Center))

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = isSystemBarVisible,
            enter = standardEnter,
            exit = standardExit,
        ) {
            controlPlayerView()
        }

        AnimatedVisibility(
            modifier = Modifier.padding(padding).align(Alignment.TopCenter),
            visible = isSystemBarVisible,
            enter = standardEnter,
            exit = standardExit,
        ) {
            appbar()
        }

        AnimatedVisibility(
            modifier = Modifier.padding(padding).align(Alignment.BottomCenter),
            visible = isSystemBarVisible,
            enter = standardEnter,
            exit = standardExit,
        ) {
            actionPlayerView()
        }
    }
}
