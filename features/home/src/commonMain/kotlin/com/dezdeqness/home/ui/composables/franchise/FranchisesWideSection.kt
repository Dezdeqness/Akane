package com.dezdeqness.home.ui.composables.franchise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.home.ui.composables.rememberHomeWideSectionLayout
import com.dezdeqness.home.ui.model.FranchisePanelUiModel

@Composable
fun FranchisesWideSection(
    modifier: Modifier = Modifier,
    franchises: List<FranchisePanelUiModel>,
    onFranchiseClicked: (FranchisePanelUiModel) -> Unit,
    onAllClicked: () -> Unit,
) {
    if (franchises.isEmpty()) return

    BoxWithConstraints(modifier = modifier) {
        val layout = rememberHomeWideSectionLayout(maxWidth)

        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Box(modifier = Modifier.widthIn(max = 600.dp)) {
                Header(
                    title = "Франшизы",
                    titleStyle = AppTheme.typography.labelLarge.copy(fontSize = layout.sectionTitleSize),
                )
                Text(
                    text = "Все",
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textSecondary,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onAllClicked() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(layout.itemSpacing),
                contentPadding = PaddingValues(start = 16.dp, end = 12.dp),
            ) {
                items(
                    items = franchises,
                    key = { it.id },
                ) { franchise ->
                    FranchisePanelItem(
                        item = franchise,
                        cardWidth = layout.cardWidth,
                        titleStyle = AppTheme.typography.bodyMedium.copy(fontSize = layout.cardTitleSize),
                        onClick = { onFranchiseClicked(franchise) },
                    )
                }
            }
        }
    }
}
