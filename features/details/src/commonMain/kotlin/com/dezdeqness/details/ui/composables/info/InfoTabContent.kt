package com.dezdeqness.details.ui.composables.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.chips.AppChip
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.details.ui.composables.core.MetadataRow
import com.dezdeqness.details.ui.model.DetailsTab

@Composable
fun InfoTabContent(info: DetailsTab.InfoTab) {
    val items = listOf(
        "Тип" to info.type,
        "Возрастной рейтинг" to info.ageRating,
        "Эпизодов" to info.episodesTotal.toString(),
        "Средняя длительность" to info.averageDuration,
        "Статус" to if (info.isOngoing) "Онгоинг" else "Завершён"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        if (info.summary.isNotEmpty()) {
            Header(
                title = "Описание",
                titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
            )
            Text(
                info.summary,
                fontSize = 14.sp,
                color = AppTheme.colors.textPrimary.copy(alpha = 0.78f),
                modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }

        OutlinedCard(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                items.forEachIndexed { index, (label, value) ->
                    MetadataRow(label, value)
                    if (index != items.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }

        if (info.genres.isNotEmpty()) {
            Header(
                title = "Жанры",
                titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
            )
            LazyRow(
                contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                items(info.genres.size) { index ->
                    AppChip(
                        onClick = {},
                        title = info.genres[index],
                        colors = FilterChipDefaults.filterChipColors().copy(
                            containerColor = AppTheme.colors.background,
                            selectedContainerColor = AppTheme.colors.background,
                        ),
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }
        }
    }
}
