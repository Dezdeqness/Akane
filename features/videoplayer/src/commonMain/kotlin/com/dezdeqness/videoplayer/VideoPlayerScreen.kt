package com.dezdeqness.videoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.videoplayer.core.player.VideoPlayerLayout
import com.dezdeqness.videoplayer.core.player.VideoSurface
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.ui.AspectRatio
import com.dezdeqness.videoplayer.core.player.feature.ui.AspectRatioFeature
import com.dezdeqness.videoplayer.ui.VideoPlayerViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoPlayerViewModel: VideoPlayerViewModel = koinViewModel(),
    onBackButtonClicked: () -> Unit = {},
) {
    val screenState by videoPlayerViewModel.screenState.collectAsStateOnLifecycle()
    val engine = videoPlayerViewModel.manager

    val aspectRatioFeature = remember(engine) {
        engine.registry.getFeature<AspectRatioFeature>(FeatureKey.AspectRatio)
    }
    val aspectRatio by aspectRatioFeature?.selectedAspectRatio?.collectAsStateOnLifecycle()
        ?: remember { mutableStateOf(AspectRatio.Fit16x9) }

    val aspectModifier = when (aspectRatio) {
        AspectRatio.Fit16x9 -> Modifier.aspectRatio(16f / 9f)
        AspectRatio.Fit4x3 -> Modifier.aspectRatio(4f / 3f)
        AspectRatio.Fill -> Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier.align(Alignment.Center).then(aspectModifier),
        ) {
            VideoSurface(engine, Modifier.fillMaxSize())
        }
        when {
            screenState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            screenState.isError -> {
                Text(
                    text = "Ошибка загрузки",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            else -> {
                val currentItem by engine.currentItem.collectAsStateOnLifecycle()

                VideoPlayerLayout(
                    engine = engine,
                    modifier = Modifier.fillMaxSize(),
                    appBarContent = {
                        AppToolbar(
                            title = {
                                Column {
                                    Text(
                                        screenState.title,
                                        style = MaterialTheme.typography.headlineMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    currentItem?.let { item ->
                                        Text(
                                            item.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                titleContentColor = Color.White,
                                containerColor = Color.Transparent,
                            ),
                            windowInsets = WindowInsets(),
                            navigation = {
                                AppIconButton(
                                    icon = AkaneIcons.Back,
                                    tint = Color.White,
                                    onClick = onBackButtonClicked,
                                )
                            },
                        )
                    },
                )
            }
        }
    }
}
