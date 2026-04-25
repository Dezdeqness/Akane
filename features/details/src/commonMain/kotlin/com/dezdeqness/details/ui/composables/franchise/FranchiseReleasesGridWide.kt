package com.dezdeqness.details.ui.composables.franchise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.details.ui.model.DetailsTab
import kotlin.collections.chunked
import kotlin.collections.forEach

@Composable
fun FranchiseReleasesGridWide(
    franchise: DetailsTab.FranchiseTab,
    modifier: Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val minCardWidth = 200.dp
        val spacing = 8.dp

        val columns = maxOf(1, (maxWidth / (minCardWidth + spacing)).toInt())

        val cardWidth = (maxWidth - (spacing * (columns - 1))) / columns

        val rows = franchise.releases.chunked(columns)

        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { release ->
                        FranchiseReleaseItemWide(
                            release = release,
                            modifier = Modifier.width(cardWidth)
                        )
                    }

                    repeat(columns - row.size) {
                        Spacer(modifier = Modifier.width(cardWidth))
                    }
                }
            }
        }
    }
}
