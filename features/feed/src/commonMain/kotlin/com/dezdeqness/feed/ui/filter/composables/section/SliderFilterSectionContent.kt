package com.dezdeqness.feed.ui.filter.composables.section

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.feed.ui.filter.SliderFilterSectionUiModel

@Composable
fun SliderFilterSectionContent(
    section: SliderFilterSectionUiModel,
    onRangeChanged: (start: Int, end: Int) -> Unit,
) {
    var sliderPosition by remember {
        mutableStateOf(section.currentStart.toFloat()..section.currentEnd.toFloat())
    }

    Column {
        Text(
            "с ${sliderPosition.start.toInt()} по ${sliderPosition.endInclusive.toInt()}",
            color = AppTheme.colors.textPrimary,
        )
        RangeSlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
                onRangeChanged(
                    sliderPosition.start.toInt(),
                    sliderPosition.endInclusive.toInt()
                )
            },
            valueRange = section.minValue.toFloat()..section.maxValue.toFloat(),
        )
    }
}
