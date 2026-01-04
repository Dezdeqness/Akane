package com.dezdeqness.details.ui.composables.info

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.chips.AppChip
import com.dezdeqness.core.ui.views.header.Header

@Composable
fun GenresSection(genres: List<String>) {
    Header(
        title = "Жанры",
        titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
    )
    LazyRow(
        contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        items(genres.size) { index ->
            AppChip(
                onClick = {},
                title = genres[index],
                colors = FilterChipDefaults.filterChipColors().copy(
                    containerColor = AppTheme.colors.background,
                    selectedContainerColor = AppTheme.colors.background,
                ),
                modifier = Modifier.padding(end = 8.dp),
            )
        }
    }
}
