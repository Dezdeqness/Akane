package com.dezdeqness.home.ui.composables.genres

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.home.ui.composables.rememberHomeWideSectionLayout
import com.dezdeqness.home.ui.model.GenrePanelUiModel

@Composable
fun GenresWideSection(
    modifier: Modifier = Modifier,
    genres: List<GenrePanelUiModel>,
    onGenreClicked: (GenrePanelUiModel) -> Unit,
    onAllClicked: () -> Unit,
) {
    if (genres.isEmpty()) return

    BoxWithConstraints(modifier = modifier) {
        val layout = rememberHomeWideSectionLayout(maxWidth)

        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Header(
                title = "Жанры",
                titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
                onClick = onAllClicked,
                modifier = Modifier.widthIn(max = 600.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(layout.itemSpacing),
                contentPadding = PaddingValues(end = 12.dp),
            ) {
                items(
                    items = genres,
                    key = { it.id },
                ) { genre ->
                    GenrePanelItem(
                        item = genre,
                        cardWidth = layout.cardWidth,
                        titleStyle = AppTheme.typography.bodyMedium.copy(fontSize = layout.cardTitleSize),
                        onClick = { onGenreClicked(genre) },
                    )
                }
            }
        }
    }
}
