package com.dezdeqness.feed.ui.filter.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dezdeqness.core.ui.views.buttons.AppButton
import com.dezdeqness.core.ui.views.buttons.AppOutlinedButton
import com.dezdeqness.feed.ui.filter.ChipFilterSectionUiModel
import com.dezdeqness.feed.ui.filter.FeedFilterSectionAction
import com.dezdeqness.feed.ui.filter.FeedFilterState
import com.dezdeqness.feed.ui.filter.FilterDisplayType
import com.dezdeqness.feed.ui.filter.SliderFilterSectionUiModel
import com.dezdeqness.feed.ui.filter.composables.section.ChipFilterSectionContent
import com.dezdeqness.feed.ui.filter.composables.section.DialogFilterSectionContent
import com.dezdeqness.feed.ui.filter.composables.section.FilterSection
import com.dezdeqness.feed.ui.filter.composables.section.SliderFilterSectionContent
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FeedFilterBottomSheetContent(
    modifier: Modifier = Modifier,
    stateFlow: StateFlow<FeedFilterState>,
    onAction: (FeedFilterSectionAction) -> Unit,
) {
    val state by stateFlow.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.sections) { section ->
                FilterSection(
                    title = section.displayName,
                    description = section.description,
                    content = {
                        when (section) {
                            is ChipFilterSectionUiModel -> {
                                when (section.displayType) {
                                    FilterDisplayType.DIALOG_SINGLE_CHOICE,
                                    FilterDisplayType.DIALOG_MULTI_CHOICE,
                                        -> {
                                        DialogFilterSectionContent(
                                            section = section,
                                            onItemsSelected = { newSelected ->
                                                onAction(
                                                    FeedFilterSectionAction.UpdateSelectedItems(
                                                        sectionId = section.id,
                                                        selectedIds = newSelected
                                                    )
                                                )
                                            }
                                        )
                                    }

                                    FilterDisplayType.CHIP_SINGLE_CHOICE,
                                    FilterDisplayType.CHIP_MULTI_CHOICE,
                                        -> {
                                        ChipFilterSectionContent(
                                            section = section,
                                            onItemClick = { itemId ->
                                                onAction(
                                                    FeedFilterSectionAction.ToggleItem(
                                                        sectionId = section.id,
                                                        itemId = itemId
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            is SliderFilterSectionUiModel -> {
                                SliderFilterSectionContent(
                                    section = section,
                                    onRangeChanged = { start, end ->
                                        onAction(
                                            FeedFilterSectionAction.UpdateRange(
                                                sectionId = section.id,
                                                start = start,
                                                end = end
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                )
            }

            item {
                Box(modifier = Modifier.height(72.dp))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppOutlinedButton(
                    title = "Сбросить",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAction(FeedFilterSectionAction.Reset)
                    },
                )
                AppButton(
                    title = "Применить",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAction(FeedFilterSectionAction.Apply)
                    },
                )
            }
        }
    }
}
