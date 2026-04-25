package com.dezdeqness.home.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.home.ui.model.HomeUiModel

@Composable
fun HomeWideSection(
    modifier: Modifier = Modifier,
    title: String,
    items: List<HomeUiModel>,
    onItemClicked: (HomeUiModel) -> Unit,
) {
    if (items.isEmpty()) return

    BoxWithConstraints(modifier = modifier) {
        val layout = rememberHomeWideSectionLayout(maxWidth)

        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = title,
                style = AppTheme.typography.labelLarge.copy(fontSize = layout.sectionTitleSize),
                color = AppTheme.colors.textPrimary,
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(layout.itemSpacing),
                contentPadding = PaddingValues(end = 12.dp),
            ) {
                items(
                    items = items,
                    key = { it.id },
                ) { item ->
                    HomeSectionItem(
                        item = item,
                        cardWidth = layout.cardWidth,
                        imageHeight = layout.imageHeight,
                        showTitle = true,
                        titleStyle = AppTheme.typography.bodySmall.copy(fontSize = layout.cardTitleSize),
                        onItemClicked = onItemClicked,
                    )
                }
            }
        }
    }
}
