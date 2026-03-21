package com.dezdeqness.downloads.ui.episodes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.theme.AppTheme

@Composable
internal fun EpisodesEmptyState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Нет скачанных эпизодов",
            color = AppTheme.colors.textSecondary,
        )
    }
}
