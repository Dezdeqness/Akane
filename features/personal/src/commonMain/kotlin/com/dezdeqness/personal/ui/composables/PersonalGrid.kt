package com.dezdeqness.personal.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dezdeqness.personal.ui.model.PersonalUiModel

@Composable
fun PersonalGrid(
    modifier: Modifier = Modifier,
    items: List<PersonalUiModel>,
    onItemClicked: (PersonalUiModel) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            PersonalCell(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onItemClicked(item) },
                item = item,
            )
        }
    }
}
