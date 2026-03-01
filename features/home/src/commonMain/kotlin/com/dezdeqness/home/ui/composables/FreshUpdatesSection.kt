package com.dezdeqness.home.ui.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dezdeqness.foundation.utils.repeatOnResumedState
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.home.ui.model.HomeUiModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreshUpdatesSection(
    modifier: Modifier = Modifier,
    items: List<HomeUiModel>,
    onItemClicked: (Long) -> Unit,
) {
    if (items.isEmpty()) return

    val shape = remember { RoundedCornerShape(28.dp) }

    val carouselState = rememberCarouselState { items.size }
    val preferredItemWidth = 150.dp
    val imageHeight = 180.dp

    Column(modifier = modifier) {
        Header(
            title = "Свежие обновления",
            titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
        )

        HorizontalMultiBrowseCarousel(
            state = carouselState,
            preferredItemWidth = preferredItemWidth,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            itemSpacing = 8.dp,
            flingBehavior = CarouselDefaults.multiBrowseFlingBehavior(
                carouselState,
                snapAnimationSpec = spring(stiffness = Spring.StiffnessMedium),
            ),
        ) { index ->
            val item = items[index]

            FreshUpdatesCarouselItem(
                modifier = Modifier.maskClip(shape),
                item = item,
                shape = shape,
                imageHeight = imageHeight,
                onClick = { onItemClicked(item.id) },
            )
        }

        val lifecycle = LocalLifecycleOwner.current.lifecycle
        LaunchedEffect(carouselState, lifecycle, items.size) {
            if (items.size <= 1) return@LaunchedEffect

            lifecycle.repeatOnResumedState {
                while (currentCoroutineContext().isActive) {
                    snapshotFlow { carouselState.isScrollInProgress }
                        .first { isScrolling -> !isScrolling }

                    delay(5000)

                    if (!currentCoroutineContext().isActive) break
                    if (carouselState.isScrollInProgress) continue

                    val nextIndex = (carouselState.currentItem + 1) % (items.size - 1)
                    carouselState.animateScrollToItem(nextIndex)
                }
            }
        }
    }
}

@Composable
private fun FreshUpdatesCarouselItem(
    modifier: Modifier = Modifier,
    item: HomeUiModel,
    shape: RoundedCornerShape,
    imageHeight: Dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier.clickable { onClick() },
    ) {
        AppImage(
            data = item.imagePath,
            contentDescription = null,
            modifier = Modifier
                .height(imageHeight)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f),
                        ),
                    ),
                    shape,
                ),
        ) {
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(all = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

