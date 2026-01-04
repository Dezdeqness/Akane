package com.dezdeqness.details.ui.composables.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            DescriptionSection(summary = info.summary)
        }

        MetadataSection(items = items)

        if (info.genres.isNotEmpty()) {
            GenresSection(genres = info.genres)
        }
    }
}
