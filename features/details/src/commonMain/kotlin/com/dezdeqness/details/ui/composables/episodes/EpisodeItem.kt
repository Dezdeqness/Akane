package com.dezdeqness.details.ui.composables.episodes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.details.ui.model.EpisodesUiModel

@Composable
fun EpisodeItem(
    episode: EpisodesUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
        }
    }
}
