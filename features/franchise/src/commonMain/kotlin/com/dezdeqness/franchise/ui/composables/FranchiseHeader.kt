package com.dezdeqness.franchise.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.designsystem.nestedscroll.LocalNestedScrollConnection
import com.dezdeqness.franchise.ui.model.FranchiseHeaderUiModel

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
fun FranchiseHeader(
    item: FranchiseHeaderUiModel,
    modifier: Modifier = Modifier,
) {
    val connection = LocalNestedScrollConnection.current
    val isScrollPass = remember {
        derivedStateOf {
            connection.progress > 0.7f
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
    ) {
        AppImage(
            data = item.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.9f),
                        ),
                    ),
                ),
        )

        AnimatedVisibility(
            visible = isScrollPass.value,
            enter = standardEnter,
            exit = standardExit,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = AppTheme.colors.surface.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                        ),
                ) {
                    AppImage(
                        data = item.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Text(
                        text = item.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )

                    if (item.meta.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.meta.joinToString(separator = "  •  "),
                            fontSize = 14.sp,
                            color = AppTheme.colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}
