package com.dezdeqness.home.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.home.ui.model.HomeUiModel

@Composable
fun HomeSection(
    modifier: Modifier = Modifier,
    title: String,
    items: List<HomeUiModel>,
    onItemClicked: (Long) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            title,
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.W600,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier,
        ) {
            items(
                count = items.size,
                key = { index -> items[index].id },
            ) { index ->
                val item = items[index]

                val paddingStart = if (index == 0) 0.dp else 4.dp
                val paddingEnd = if (index < items.size - 1) 4.dp else 0.dp

                HomeSectionItem(
                    modifier = Modifier.padding(start = paddingStart, end = paddingEnd),
                    item = item,
                    onItemClicked = onItemClicked,
                )
            }
        }
    }
}
