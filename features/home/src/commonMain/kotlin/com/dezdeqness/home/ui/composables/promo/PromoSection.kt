package com.dezdeqness.home.ui.composables.promo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.home.ui.model.PromoPanelUiModel

@Composable
fun PromoSection(
    modifier: Modifier = Modifier,
    promos: List<PromoPanelUiModel>,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
) {
    if (promos.isEmpty()) return

    PromoPager(
        modifier = modifier,
        promos = promos,
        bannerHeight = 160.dp,
        horizontalPadding = 16.dp,
        onReleaseClicked = onReleaseClicked,
    )
}
