package com.dezdeqness.stories.ui.navigation

import com.dezdeqness.stories.ui.StoriesEvent
import com.dezdeqness.stories.ui.StoriesState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class StoriesNavigator(private val initialCollection: Int) {

    private val _position = MutableStateFlow(StoriesPosition())
    val position: StateFlow<StoriesPosition> = _position.asStateFlow()

    private val _events = Channel<StoriesEvent>(Channel.BUFFERED)
    val events: Flow<StoriesEvent> = _events.receiveAsFlow()

    fun resolve(content: StoriesState, position: StoriesPosition): StoriesState {
        val collections = content.collections

        val currentCollection = collections.indexOfFirst { it.id == position.dayId }
            .takeIf { it >= 0 }
            ?: initialCollection.coerceIn(0, (collections.size - 1).coerceAtLeast(0))

        val storyIndexes = collections.map { collection ->
            val openedEpisodeId = position.openedEpisodes[collection.id]
            collection.stories.indexOfFirst { it.episodeId == openedEpisodeId }.takeIf { it >= 0 } ?: 0
        }

        return content.copy(storyIndexes = storyIndexes, currentCollection = currentCollection)
    }


    fun next(state: StoriesState) {
        val collection = state.collections.getOrNull(state.currentCollection) ?: return

        val storyIndex = state.storyIndexOf(state.currentCollection)

        when {
            storyIndex < collection.stories.lastIndex -> open(state, storyIndex + 1)
            state.currentCollection < state.collections.lastIndex ->
                _events.trySend(StoriesEvent.ScrollToCollection(state.currentCollection + 1))

            else -> _events.trySend(StoriesEvent.Close)
        }
    }

    fun previous(state: StoriesState): Boolean {
        val storyIndex = state.storyIndexOf(state.currentCollection)

        return when {
            storyIndex > 0 -> {
                open(state, storyIndex - 1)
                true
            }

            state.currentCollection > 0 -> {
                _events.trySend(StoriesEvent.ScrollToCollection(state.currentCollection - 1))
                true
            }

            else -> false
        }
    }

    fun onCollectionSettled(state: StoriesState, index: Int) {
        val dayId = state.collections.getOrNull(index)?.id ?: return

        _position.update { it.copy(dayId = dayId) }
    }

    private fun open(state: StoriesState, storyIndex: Int) {
        val collection = state.collections.getOrNull(state.currentCollection) ?: return

        val episodeId = collection.stories.getOrNull(storyIndex)?.episodeId ?: return

        _position.update { it.copy(openedEpisodes = it.openedEpisodes + (collection.id to episodeId)) }
    }
}

data class StoriesPosition(
    val dayId: Int? = null,
    val openedEpisodes: Map<Int, String> = emptyMap(),
)
