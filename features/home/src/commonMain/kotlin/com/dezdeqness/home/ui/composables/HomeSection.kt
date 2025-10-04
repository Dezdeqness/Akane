package com.dezdeqness.home.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.core.ui.views.sections.AppSections
import com.dezdeqness.home.ui.model.HomeUiModel

@Composable
fun HomeSection(
    modifier: Modifier = Modifier,
    title: String,
    items: List<HomeUiModel>,
    onItemClicked: (Long) -> Unit,
) {
    AppSections(
        modifier = modifier,
        header = {
            Header(title = title)
        },
        items = items,
        itemContent = { item ->
            HomeSectionItem(
                item = item,
                onItemClicked = onItemClicked,
            )
        }
    )
}
