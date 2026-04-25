package com.dezdeqness.home.ui.composables.freshupdates

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.home.ui.composables.rememberHomeWideSectionLayout
import com.dezdeqness.home.ui.model.HomeUiModel
import kotlinx.coroutines.delay

@Composable
fun FreshUpdatesWideSection(
    modifier: Modifier = Modifier,
    items: List<HomeUiModel>,
    onItemClicked: (HomeUiModel) -> Unit,
) {
    if (items.isEmpty()) return

    var selectedIndex by rememberSaveable(items.map(HomeUiModel::id)) {
        mutableIntStateOf(0)
    }
    val resolvedIndex = selectedIndex.coerceIn(items.indices)
    val selectedItem = items[resolvedIndex]

    LaunchedEffect(items) {
        while (true) {
            delay(5000)
            selectedIndex = (selectedIndex + 1) % items.size
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val layout = rememberHomeWideSectionLayout(maxWidth)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Top,
        ) {
            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = selectedItem,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith
                            fadeOut(animationSpec = tween(500))
                },
                label = "featured_switch"
            ) { item ->
                FeaturedFreshUpdatesWideCard(
                    item = item,
                    height = layout.featuredCardHeight,
                    onItemClicked = onItemClicked,
                )
            }

            FreshUpdatesWideShelf(
                items = items,
                selectedItemId = selectedItem.id,
                height = layout.featuredCardHeight,
                width = layout.sidebarWidth,
                onItemSelected = { item ->
                    selectedIndex = items.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                },
            )
        }
    }
}
