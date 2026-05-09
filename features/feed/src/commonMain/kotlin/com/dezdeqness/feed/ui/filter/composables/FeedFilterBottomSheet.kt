package com.dezdeqness.feed.ui.filter.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.feed.contract.model.CatalogFilter
import com.dezdeqness.feed.ui.filter.FeedFilterEvent
import com.dezdeqness.feed.ui.filter.FeedFilterViewModel
import com.dezdeqness.foundation.utils.collectEvents
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedFilterBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: FeedFilterViewModel = koinViewModel(),
    catalogFilter: CatalogFilter,
    onClosed: () -> Unit,
    onFilterChanged: (CatalogFilter) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            newValue != SheetValue.Hidden
        },
    )

    LaunchedEffect(Unit) {
        viewModel.onFeedFilterShown(catalogFilter)
    }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onClosed() },
        sheetState = sheetState,
        containerColor = AppTheme.colors.background,
        dragHandle = null,
        sheetGesturesEnabled = false,
        properties = ModalBottomSheetProperties()
    ) {
        FeedFilterBottomSheetContent(
            modifier = modifier,
            stateFlow = viewModel.sectionsState,
            onAction = viewModel::onAction
        )
    }

    viewModel.events.collectEvents { event ->
        when (event) {
            is FeedFilterEvent.ApplyFilter -> {
                onFilterChanged(event.catalogFilter)
                onClosed()
            }
        }
    }

}
