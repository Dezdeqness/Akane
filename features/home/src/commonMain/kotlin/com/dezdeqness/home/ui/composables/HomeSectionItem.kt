package com.dezdeqness.home.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.home.ui.model.HomeUiModel

@Composable
fun HomeSectionItem(
    modifier: Modifier = Modifier,
    item: HomeUiModel,
    cardWidth: Dp = 100.dp,
    imageHeight: Dp = 120.dp,
    showTitle: Boolean = false,
    titleStyle: TextStyle = AppTheme.typography.bodySmall,
    onItemClicked: (HomeUiModel) -> Unit,
) {
    Column(
        modifier = modifier
            .width(cardWidth)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onItemClicked(item) },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AppImage(
            data = item.imagePath,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clip(RoundedCornerShape(18.dp)),
        )

        if (showTitle) {
            Text(
                text = item.name,
                style = titleStyle,
                color = AppTheme.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}
