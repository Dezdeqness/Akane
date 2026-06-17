package com.dezdeqness.home.ui.composables.genres

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.home.ui.model.GenrePanelUiModel

@Composable
fun GenresSection(
    modifier: Modifier = Modifier,
    genres: List<GenrePanelUiModel>,
    onGenreClicked: (GenrePanelUiModel) -> Unit,
    onAllClicked: () -> Unit,
) {
    if (genres.isEmpty()) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Header(
            title = "Жанры",
            titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
            onClick = onAllClicked,
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(
                items = genres,
                key = { it.id },
            ) { genre ->
                GenrePanelItem(
                    item = genre,
                    cardWidth = 116.dp,
                    titleStyle = AppTheme.typography.bodyMedium,
                    onClick = { onGenreClicked(genre) },
                )
            }
        }
    }
}
