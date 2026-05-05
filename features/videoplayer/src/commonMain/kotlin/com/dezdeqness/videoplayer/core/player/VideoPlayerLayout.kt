package com.dezdeqness.videoplayer.core.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.dezdeqness.foundation.config.getDeviceConfiguration
import com.dezdeqness.videoplayer.core.player.feature.ControlSlot
import com.dezdeqness.videoplayer.core.player.composables.slider.ProgressSliderState
import com.dezdeqness.videoplayer.core.player.composables.slider.VideoProgressSlider

private val controlsEnter = fadeIn(tween(250, easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)))
private val controlsExit = fadeOut(tween(200, easing = CubicBezierEasing(0.3f, 0f, 1f, 1f)))

@Composable
fun VideoPlayerLayout(
    engine: VideoPlayerManager,
    modifier: Modifier = Modifier,
    appBarContent: @Composable () -> Unit = {},
) {
    val controlsVisible by engine.controlsVisible.collectAsStateOnLifecycle()
    val lockedControlsVisible by engine.lockedControlsVisible.collectAsStateOnLifecycle()
    val isLocked by engine.isLocked.collectAsStateOnLifecycle()

    val safePadding = WindowInsets.safeDrawing.asPaddingValues()
    val isPortrait = getDeviceConfiguration().isPortrait

    val outerPadding = if (isPortrait) {
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

    val bottomPadding by animateDpAsState(
        targetValue = if (controlsVisible && !isLocked) {
            if (isPortrait) 160.dp else 80.dp
        } else {
            16.dp
        },
        label = "overlay_padding"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(engine.registry.combinedModifier()),
    ) {
        AnimatedVisibility(
            visible = controlsVisible && !isLocked,
            enter = controlsEnter,
            exit = controlsExit,
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(outerPadding)) {

                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        engine.registry.SlotContent(ControlSlot.TopStart)
                        appBarContent()
                        engine.registry.SlotContent(ControlSlot.TopEnd)
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    VideoPlayerControls(context = engine)
                    engine.registry.SlotContent(ControlSlot.Center)
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                ) {
                    BottomRow(engine = engine, isPortrait = isPortrait)
                }
            }
        }

        AnimatedVisibility(
            visible = if (isLocked) lockedControlsVisible else controlsVisible,
            enter = controlsEnter,
            exit = controlsExit,
            modifier = Modifier.align(Alignment.CenterEnd).padding(outerPadding),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                engine.registry.SlotContent(ControlSlot.CenterEnd)
            }
        }

        Box(
            modifier = Modifier
                .align(if (isPortrait) Alignment.BottomCenter else Alignment.BottomEnd)
                .padding(outerPadding)
                .padding(bottom = bottomPadding),
            contentAlignment = Alignment.Center,
        ) {
            engine.registry.SlotContent(ControlSlot.Overlay)
        }
    }
}

@Composable
private fun BottomRow(
    engine: VideoPlayerManager,
    isPortrait: Boolean,
    modifier: Modifier = Modifier,
) {
    val playerState by engine.playerState.collectAsStateOnLifecycle()
    val currentItem by engine.currentItem.collectAsStateOnLifecycle()

    if (isPortrait) {
        Column(
            modifier = modifier.fillMaxWidth().padding(16.dp),
        ) {
            engine.registry.SlotContent(ControlSlot.BottomStart)
            VideoProgressSlider(
                modifier = Modifier.fillMaxWidth(),
                state = ProgressSliderState(
                    position = playerState.position,
                    duration = playerState.duration,
                    buffered = playerState.buffered,
                ),
                onSeek = engine::seekTo,
                opening = currentItem?.opening,
                ending = currentItem?.ending,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                engine.registry.SlotContent(ControlSlot.BottomActionsStart)
                engine.registry.SlotContent(ControlSlot.BottomEnd)
                engine.registry.SlotContent(ControlSlot.BottomActionsEnd)
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth().padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                engine.registry.SlotContent(ControlSlot.BottomStart)
                VideoProgressSlider(
                    modifier = Modifier.weight(1f),
                    state = ProgressSliderState(
                        position = playerState.position,
                        duration = playerState.duration,
                        buffered = playerState.buffered,
                    ),
                    onSeek = engine::seekTo,
                    opening = currentItem?.opening,
                    ending = currentItem?.ending,
                )
                Row { engine.registry.SlotContent(ControlSlot.BottomEnd) }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    engine.registry.SlotContent(ControlSlot.BottomActionsStart)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    engine.registry.SlotContent(ControlSlot.BottomActionsEnd)
                }
            }
        }
    }
}
