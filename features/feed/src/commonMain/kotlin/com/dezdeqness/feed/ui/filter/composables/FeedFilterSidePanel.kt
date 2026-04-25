package com.dezdeqness.feed.ui.filter.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.dezdeqness.feed.domain.model.CatalogFilter
import com.dezdeqness.feed.ui.filter.FeedFilterEvent
import com.dezdeqness.feed.ui.filter.FeedFilterViewModel
import com.dezdeqness.foundation.utils.collectEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedFilterSidePanel(
    modifier: Modifier = Modifier,
    viewModel: FeedFilterViewModel = koinViewModel(),
    catalogFilter: CatalogFilter,
    closeOnApply: Boolean = true,
    onClosed: () -> Unit,
    onFilterChanged: (CatalogFilter) -> Unit,
) {
    LaunchedEffect(catalogFilter) {
        viewModel.onFeedFilterShown(catalogFilter)
    }

    FeedFilterBottomSheetContent(
        modifier = modifier,
        stateFlow = viewModel.sectionsState,
        onAction = viewModel::onAction,
    )

    viewModel.events.collectEvents { event ->
        when (event) {
            is FeedFilterEvent.ApplyFilter -> {
                onFilterChanged(event.catalogFilter)
                if (closeOnApply) {
                    onClosed()
                }
            }
        }
    }

}
