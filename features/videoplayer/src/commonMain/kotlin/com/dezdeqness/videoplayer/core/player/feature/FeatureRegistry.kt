package com.dezdeqness.videoplayer.core.player.feature

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class FeatureRegistry {

    private val features = LinkedHashMap<FeatureKey, PlayerFeature>()

    internal fun register(feature: PlayerFeature) {
        features[feature.key] = feature
    }

    fun isActive(key: FeatureKey): Boolean = key in features

    @Suppress("UNCHECKED_CAST")
    fun <T : PlayerFeature> getFeature(key: FeatureKey): T? = features[key] as? T

    fun allFeatures(): Collection<PlayerFeature> = features.values

    fun combinedModifier(): Modifier =
        features.values.fold(Modifier as Modifier) { acc, feature -> acc.then(feature.modifier()) }

    @Composable
    fun SlotContent(slot: ControlSlot) {
        features.values
            .filterIsInstance<UiFeature>()
            .filter { slot in it.slots }
            .forEach { it.Content(slot) }
    }
}
