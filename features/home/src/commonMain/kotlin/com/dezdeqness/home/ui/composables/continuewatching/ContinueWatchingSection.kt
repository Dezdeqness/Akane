package com.dezdeqness.home.ui.composables.continuewatching

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.home.ui.model.ContinueWatchingUiModel

@Composable
fun ContinueWatchingSection(
    item: ContinueWatchingUiModel?,
    onItemClicked: (ContinueWatchingUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (item == null) return

    Column(modifier = modifier) {
        Header(
            title = "Продолжить просмотр",
            titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
        )

        ContinueWatchingCard(
            item = item,
            onClick = { onItemClicked(item) },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(180.dp),
        )
    }
}
