package com.dezdeqness.videoplayer

import akane.features.videoplayer.generated.resources.Res
import akane.features.videoplayer.generated.resources.ic_next
import akane.features.videoplayer.generated.resources.ic_retry
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.designsystem.utils.noRippleClickable
import com.dezdeqness.foundation.config.getDeviceConfiguration
import com.dezdeqness.videoplayer.core.player.EpisodeEndOverlayUiState
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

private const val AUTO_NEXT_DURATION_MS = 5_000L
private const val AUTO_NEXT_TIMER_STEP_MS = 100L

@Composable
fun EpisodeEndOverlay(
    state: EpisodeEndOverlayUiState,
    onAutoNext: (nextIndex: Int) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state is EpisodeEndOverlayUiState.Hidden) return

    val autoNextState = state as? EpisodeEndOverlayUiState.AutoNext
    val retryState = state as? EpisodeEndOverlayUiState.Retry

    var remainingMs by rememberSaveable(state) {
        mutableLongStateOf(AUTO_NEXT_DURATION_MS)
    }

    LaunchedEffect(autoNextState) {
        if (autoNextState == null) {
            remainingMs = AUTO_NEXT_DURATION_MS
            return@LaunchedEffect
        }

        remainingMs = AUTO_NEXT_DURATION_MS

        while (remainingMs > 0L) {
            delay(AUTO_NEXT_TIMER_STEP_MS)
            remainingMs = (remainingMs - AUTO_NEXT_TIMER_STEP_MS).coerceAtLeast(0L)
        }

        onAutoNext(autoNextState.nextIndex)
    }

    val previewUrl = autoNextState?.previewUrl ?: retryState?.previewUrl.orEmpty()
    val isAutoNext = autoNextState != null
    val secondsLeft = ((remainingMs + 999L) / 1000L).coerceAtLeast(0L)
    val progress = if (isAutoNext) {
        (remainingMs.toFloat() / AUTO_NEXT_DURATION_MS.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        AppImage(
            data = previewUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f)),
        )

        val outerPadding = rememberEpisodeEndOverlayPadding()

        AppToolbar(
            modifier = Modifier
                .padding(outerPadding)
                .align(Alignment.TopCenter),
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                titleContentColor = Color.White,
                containerColor = Color.Transparent,
            ),
            windowInsets = WindowInsets(),
            navigation = {
                AppIconButton(
                    icon = AkaneIcons.Back,
                    tint = Color.White,
                    onClick = onBack,
                )
            },
        )

        if (isAutoNext) {
            AutoNextContent(
                secondsLeft = secondsLeft,
                progress = progress,
                onClick = { onAutoNext(autoNextState.nextIndex) },
            )
        } else {
            RetryContent(onRetry = onRetry)
        }
    }
}

@Composable
private fun rememberEpisodeEndOverlayPadding(): PaddingValues {
    val safePadding = WindowInsets.safeDrawing.asPaddingValues()
    val isPortrait = getDeviceConfiguration().isPortrait

    return if (isPortrait) {
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
}

@Composable
private fun AutoNextContent(
    secondsLeft: Long,
    progress: Float,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(96.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.25f),
                strokeWidth = 4.dp,
            )

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .noRippleClickable(onClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_next),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Next episode in ${secondsLeft}s",
            color = Color.White,
        )
    }
}

@Composable
private fun RetryContent(
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .noRippleClickable(onRetry),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_retry),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp),
        )
    }
}