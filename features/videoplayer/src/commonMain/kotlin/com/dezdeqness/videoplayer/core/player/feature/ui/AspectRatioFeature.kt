package com.dezdeqness.videoplayer.engine.feature.aspect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.core.ui.views.buttons.AppOutlinedButton
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.feature.ControlSlot
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.UiFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AspectRatio(val label: String) {
    Fit16x9("16:9"),
    Fit4x3("4:3"),
    Fill("Fill"),
}

class AspectRatioFeature(
    initialRatio: AspectRatio = AspectRatio.Fit16x9,
) : UiFeature {

    override val key: FeatureKey = FeatureKey.AspectRatio
    override val slots: Set<ControlSlot> = setOf(ControlSlot.BottomEnd)

    private val _selectedAspectRatio = MutableStateFlow(initialRatio)
    val selectedAspectRatio: StateFlow<AspectRatio> = _selectedAspectRatio.asStateFlow()

    override fun install(context: PlayerContext, scope: CoroutineScope) {}
    override fun dispose() {}

    fun cycleAspectRatio() {
        val values = AspectRatio.entries
        val next = values[(values.indexOf(_selectedAspectRatio.value) + 1) % values.size]
        _selectedAspectRatio.value = next
    }

    @Composable
    override fun Content(slot: ControlSlot) {
        val ratio by selectedAspectRatio.collectAsStateOnLifecycle()
        AppOutlinedButton(
            title = ratio.label,
            onClick = ::cycleAspectRatio,
        )
    }
}
