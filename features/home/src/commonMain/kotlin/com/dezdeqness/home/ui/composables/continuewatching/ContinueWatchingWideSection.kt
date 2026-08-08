package com.dezdeqness.home.ui.composables.continuewatching

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.dezdeqness.home.ui.model.ContinueWatchingUiModel

@Composable
fun ContinueWatchingWideSection(
    item: ContinueWatchingUiModel?,
    cardHeight: Dp,
    onItemClicked: (ContinueWatchingUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (item == null) return

    ContinueWatchingCard(
        item = item,
        label = "Продолжить просмотр",
        onClick = { onItemClicked(item) },
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight),
    )
}
