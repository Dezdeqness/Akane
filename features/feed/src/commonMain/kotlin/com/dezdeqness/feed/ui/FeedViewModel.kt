package com.dezdeqness.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.feed.domain.repository.FeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedRepository: FeedRepository,
    private val feedUiMapper: FeedUiMapper,
) : ViewModel() {

    private val _feedStateFlow: MutableStateFlow<FeedState> = MutableStateFlow(FeedState())
    val feedStateFlow: StateFlow<FeedState> = _feedStateFlow

    fun onInitialLoad() {
        viewModelScope.launch(Dispatchers.IO) {
            _feedStateFlow.update {
                it.copy(isLoading = true)
            }
            feedRepository
                .getFeed(page = 1)
                .onSuccess { feed ->
                    _feedStateFlow.update {
                        it.copy(
                            isLoading = false,
                            items = feed.items.map(feedUiMapper::map),
                        )
                    }
                }
                .onFailure { throwable ->
                    _feedStateFlow.update {
                        it.copy(isLoading = true)
                    }
                    // TODO: logger for each platform
                    println("Error: $throwable")
                }
        }
    }
}
