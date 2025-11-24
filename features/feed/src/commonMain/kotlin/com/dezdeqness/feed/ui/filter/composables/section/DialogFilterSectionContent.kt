package com.dezdeqness.feed.ui.filter.composables.section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.feed.ui.filter.ChipFilterSectionUiModel
import com.dezdeqness.feed.ui.filter.FilterDisplayType
import com.dezdeqness.feed.ui.filter.composables.dialog.GenresMultiSelectDialog
import com.dezdeqness.feed.ui.filter.composables.dialog.SingleSelectDialog

@Composable
fun DialogFilterSectionContent(
    section: ChipFilterSectionUiModel,
    onItemsSelected: (Set<String>) -> Unit,
) {
    var dialogVisible by remember { mutableStateOf(false) }

    val selectedItems = section.items.filter { it.id in section.selectedCells }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.onPrimary)
            .clickable { dialogVisible = true }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            if (selectedItems.isEmpty()) {
                Text(
                    text = section.placeholder ?: "Укажите ${section.displayName.lowercase()}",
                    color = AppTheme.colors.textSecondary,
                    style = AppTheme.typography.bodyMedium,
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    selectedItems.forEach { item ->
                        FilterChip(
                            selected = true,
                            onClick = { dialogVisible = true },
                            label = {
                                Text(
                                    text = item.title,
                                    color = AppTheme.colors.textPrimary,
                                    style = AppTheme.typography.bodyMedium,
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = AkaneIcons.Close,
                                    contentDescription = "Удалить",
                                    tint = AppTheme.colors.textPrimary,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                        ) {
                                            val newSelected = section.selectedCells - item.id
                                            onItemsSelected(newSelected)
                                        }
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = AppTheme.colors.background,
                                labelColor = AppTheme.colors.textPrimary,
                                selectedContainerColor = AppTheme.colors.background,
                                selectedLabelColor = AppTheme.colors.textPrimary,
                            ),
                            shape = RoundedCornerShape(50),
                        )
                    }
                }
            }
        }

        Icon(
            imageVector = AkaneIcons.ArrowDropDown,
            contentDescription = null,
            tint = AppTheme.colors.textSecondary,
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.CenterVertically),
        )
    }

    if (dialogVisible) {
        when (section.displayType) {
            FilterDisplayType.DIALOG_SINGLE_CHOICE -> {
                SingleSelectDialog(
                    section = section,
                    onDismiss = { dialogVisible = false },
                    onSave = { selectedId ->
                        val newSelected = selectedId?.let { setOf(it) } ?: emptySet()
                        onItemsSelected(newSelected)
                        dialogVisible = false
                    },
                )
            }

            FilterDisplayType.DIALOG_MULTI_CHOICE -> {
                GenresMultiSelectDialog(
                    section = section,
                    onDismiss = { dialogVisible = false },
                    onSave = { newSelected ->
                        onItemsSelected(newSelected)
                        dialogVisible = false
                    },
                )
            }

            else -> {}
        }
    }
}
