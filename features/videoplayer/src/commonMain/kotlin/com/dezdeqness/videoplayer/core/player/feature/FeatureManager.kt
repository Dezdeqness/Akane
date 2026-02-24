package com.dezdeqness.videoplayer.core.player.feature

import androidx.compose.ui.Modifier
import com.dezdeqness.videoplayer.core.player.VideoPlayerController

class FeatureManager(
    private val features: List<PlayerFeature>,
    private val controller: VideoPlayerController,
) {
    fun installAll() = features.forEach { it.install(controller) }
    fun disposeAll() = features.forEach { it.dispose() }

    fun combinedModifier(): Modifier = features.fold(initial = (Modifier as Modifier)) { acc, f ->
        acc.then(f.modifier())
    }
}
