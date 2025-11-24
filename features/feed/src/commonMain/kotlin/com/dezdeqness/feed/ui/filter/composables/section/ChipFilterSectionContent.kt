package com.dezdeqness.feed.ui.filter.composables.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.chips.AppChip
import com.dezdeqness.feed.ui.filter.ChipFilterSectionUiModel

@Composable
fun ChipFilterSectionContent(
    section: ChipFilterSectionUiModel,
    onItemClick: (itemId: String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        section.items.forEach { item ->
            val isSelected = item.id in section.selectedCells

            val color = if (isSelected) {
                AppTheme.colors.primary
            } else {
                AppTheme.colors.background
            }

            AppChip(
                title = item.title,
                onClick = { onItemClick(item.id) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = color,
                ),
            )
        }
    }
}
