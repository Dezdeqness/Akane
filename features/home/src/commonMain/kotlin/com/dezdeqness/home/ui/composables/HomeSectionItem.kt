package com.dezdeqness.home.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.home.ui.model.HomeUiModel

@Composable
fun HomeSectionItem(
    modifier: Modifier = Modifier,
    item: HomeUiModel,
    onItemClicked: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                onClick = {
                    onItemClicked.invoke()
                },
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AppImage(
            data = item.imagePath,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
    }
}
