package com.dezdeqness.videoplayer.core.player.feature.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.composables.dropdown.QualityDropdownMenu
import com.dezdeqness.videoplayer.core.player.data.MediaQuality
import com.dezdeqness.videoplayer.core.player.data.QualityVariant
import com.dezdeqness.videoplayer.core.player.data.qualityVariants
import com.dezdeqness.videoplayer.core.player.feature.ControlSlot
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.UiFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class QualityFeature : UiFeature {

    override val key: FeatureKey = FeatureKey.Quality
    override val slots: Set<ControlSlot> = setOf(ControlSlot.BottomEnd)

    private var playerContext: PlayerContext? = null

    private val _variants = MutableStateFlow<List<QualityVariant>>(emptyList())
    val variants: StateFlow<List<QualityVariant>> = _variants.asStateFlow()

    private val _menuVisible = MutableStateFlow(false)

    override fun install(context: PlayerContext, scope: CoroutineScope) {
        playerContext = context
        scope.launch {
            context.currentItem.collect { item ->
                val newVariants = item?.source?.qualityVariants() ?: emptyList()
                _variants.value = newVariants

                val current = context.selectedQuality.value
                if (newVariants.none { it.quality == current }) {
                    newVariants.lastOrNull()?.quality?.let {
                        context.setQuality(it)
                    }
                }
            }
        }
    }

    override fun dispose() {
        playerContext = null
    }

    fun selectVariant(variant: MediaQuality) {
        _menuVisible.value = false
        playerContext?.resumeAutoHide()
        playerContext?.setQuality(variant)
    }

    @Composable
    override fun Content(slot: ControlSlot) {
        val variants by _variants.collectAsStateOnLifecycle()
        val selected by (playerContext?.selectedQuality ?: return).collectAsStateOnLifecycle()
        val menuVisible by _menuVisible.collectAsStateOnLifecycle()

        if (variants.size <= 1) return

        val selectedLabel = variants.firstOrNull { it.quality == selected }?.quality?.nameQuality
            ?: variants.lastOrNull()?.quality?.nameQuality
            ?: return

        Box {
            AppIconButton(
                onClick = {
                    _menuVisible.value = true
                    playerContext?.pauseAutoHide()
                },
                contentColor = Color.Black.copy(alpha = 0.5f)
            ) {
                Text(selectedLabel, color = Color.White)
            }
            QualityDropdownMenu(
                isExpanded = menuVisible,
                variants = variants,
                currentQuality = selected,
                onQualityChange = ::selectVariant,
                onDismiss = {
                    _menuVisible.value = false
                    playerContext?.resumeAutoHide()
                },
            )
        }
    }
}
