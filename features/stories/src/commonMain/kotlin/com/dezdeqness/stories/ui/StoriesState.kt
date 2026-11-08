package com.dezdeqness.stories.ui

import androidx.compose.runtime.Immutable
import com.dezdeqness.stories.ui.model.StoryCollectionUiModel
import com.dezdeqness.stories.ui.model.StoryUiModel

@Immutable
data class StoriesState(
    val status: Status = Status.Loading,
    val collections: List<StoryCollectionUiModel> = emptyList(),
    val storyIndexes: List<Int> = emptyList(),
    val currentCollection: Int = 0,
) {
    enum class Status { Loading, Error, Empty, Loaded }

    fun storyIndexOf(collection: Int) = storyIndexes.getOrElse(collection) { 0 }

    fun activeStory(): StoryUiModel? = storyAt(currentCollection, storyIndexOf(currentCollection))

    fun nextStory(): StoryUiModel? =
        storyAt(currentCollection, storyIndexOf(currentCollection) + 1)
            ?: storyAt(currentCollection + 1, storyIndexOf(currentCollection + 1))

    fun previousStory(): StoryUiModel? =
        storyAt(currentCollection, storyIndexOf(currentCollection) - 1)
            ?: storyAt(currentCollection - 1, storyIndexOf(currentCollection - 1))

    private fun storyAt(collection: Int, index: Int): StoryUiModel? =
        collections.getOrNull(collection)?.stories?.getOrNull(index)
}

sealed interface StoriesEvent {
    data class ScrollToCollection(val index: Int) : StoriesEvent
    data object Close : StoriesEvent
}
