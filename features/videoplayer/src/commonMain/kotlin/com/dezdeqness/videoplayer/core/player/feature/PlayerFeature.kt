package com.dezdeqness.videoplayer.core.player.feature

import androidx.compose.ui.Modifier
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import kotlinx.coroutines.CoroutineScope

interface PlayerFeature {
    val key: FeatureKey

    fun install(context: PlayerContext, scope: CoroutineScope)

    fun modifier(): Modifier = Modifier

    fun dispose()
}
