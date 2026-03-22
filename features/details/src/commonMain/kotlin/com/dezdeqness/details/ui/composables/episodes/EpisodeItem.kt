package com.dezdeqness.details.ui.composables.episodes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.details.ui.model.DownloadStatusUi
import com.dezdeqness.details.ui.model.EpisodesUiModel

@Composable
fun EpisodeItem(
    episode: EpisodesUiModel,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onCancelDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box {
            AppImage(
                data = episode.previewUrl,
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    Color.Gray,
                    blendMode = BlendMode.Darken
                ),
                modifier = Modifier
                    .height(200.dp)
                    .blur(
                        radiusX = 10.dp,
                        radiusY = 10.dp,
                        edgeTreatment = BlurredEdgeTreatment(
                            RoundedCornerShape(8.dp)
                        )
                    )
            )

            Column(modifier = Modifier.align(alignment = Alignment.BottomStart)) {
                Text(
                    episode.name,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.White.copy(alpha = 0.78f),
                )

                Text(
                    "${episode.ordinal} эпизод",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            if (episode.episodeUrls.isNotEmpty()) {
                when (episode.downloadStatus) {
                    null,
                    DownloadStatusUi.FAILED,
                        -> {
                        AppIconButton(
                            onClick = onDownloadClick,
                            modifier = Modifier.align(Alignment.TopEnd),
                            contentColor = Color.Transparent,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = Color.White,
                            )
                        }
                    }

                    DownloadStatusUi.QUEUED,
                    DownloadStatusUi.DOWNLOADING,
                    DownloadStatusUi.PAUSED,
                        -> {
                        Row(modifier = Modifier.align(Alignment.TopEnd)) {
                            if (episode.downloadStatus == DownloadStatusUi.PAUSED) {
                                Icon(
                                    imageVector = Icons.Default.Pause,
                                    contentDescription = "Paused",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .size(24.dp),
                                )
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                )
                            }
                            AppIconButton(
                                onClick = onCancelDownloadClick,
                                contentColor = Color.Transparent,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = Color.White,
                                )
                            }
                        }
                    }

                    DownloadStatusUi.COMPLETED -> {
                        AppIconButton(
                            onClick = {},
                            modifier = Modifier.align(Alignment.TopEnd),
                            contentColor = Color.Transparent,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Downloaded",
                                tint = Color(0xFF4CAF50),
                            )
                        }
                    }
                }
            }
        }
    }
}
