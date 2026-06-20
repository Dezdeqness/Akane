package com.dezdeqness.home.ui.composables.promo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.home.ui.model.PromoPanelUiModel
import com.dezdeqness.promo.contract.model.PromoTarget
import kotlinx.coroutines.delay

@Composable
fun PromoPager(
    promos: List<PromoPanelUiModel>,
    bannerHeight: Dp,
    horizontalPadding: Dp,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (promos.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { promos.size })
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(promos.size) {
        if (promos.size <= 1) return@LaunchedEffect
        while (true) {
            delay(6000)
            val next = (pagerState.currentPage + 1) % promos.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            pageSpacing = 8.dp,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val item = promos[page]
            PromoBannerCard(
                item = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bannerHeight),
                onClick = {
                    when (val target = item.target) {
                        is PromoTarget.Link -> uriHandler.openUri(target.url)
                        is PromoTarget.Release -> onReleaseClicked(target.releaseId, item.title.orEmpty())
                        PromoTarget.None -> Unit
                    }
                },
            )
        }

        if (promos.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            ) {
                repeat(promos.size) { index ->
                    val selected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (selected) 18.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) AppTheme.colors.textPrimary
                                else AppTheme.colors.textSecondary.copy(alpha = 0.4f)
                            ),
                    )
                }
            }
        }
    }
}

@Composable
fun PromoBannerCard(
    item: PromoPanelUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
    ) {
        AppImage(
            data = item.imageUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        if (item.hasOverlay && !item.title.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        ),
                    ),
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = item.title,
                        style = AppTheme.typography.labelLarge,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!item.actionLabel.isNullOrBlank()) {
                        Text(
                            text = item.actionLabel,
                            style = AppTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                        )
                    }
                }
            }
        }

        if (item.isAd) {
            Text(
                text = "Реклама",
                style = AppTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
}
