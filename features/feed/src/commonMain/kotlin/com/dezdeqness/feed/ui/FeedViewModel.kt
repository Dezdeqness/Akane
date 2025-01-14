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

    private var currentPage = INITIAL_PAGE

    init {
        onInitialLoad()
    }

    private fun onInitialLoad() {
        viewModelScope.launch(Dispatchers.IO) {
            _feedStateFlow.update {
                it.copy(isLoading = true)
            }
            feedRepository
                .getFeed(page = currentPage)
                .onSuccess { feed ->
                    currentPage = feed.page
                    _feedStateFlow.update {
                        it.copy(
                            isLoading = false,
                            items = feed.items.map(feedUiMapper::map),
                            hasNextPage = feed.hasNextPage,
                        )
                    }
                }
                .onFailure { throwable ->
                    _feedStateFlow.update {
                        it.copy(
                            isLoading = false,
                            hasNextPage = false,
                        )
                    }
                    // TODO: logger for each platform
                    println("Error: $throwable")
                }
        }
    }

    fun onLoadMore() {
        viewModelScope.launch(Dispatchers.IO) {
            feedRepository
                .getFeed(page = currentPage)
                .onSuccess { feed ->
                    currentPage = feed.page
                    val paginatedData = feed.items.map(feedUiMapper::map)

                    _feedStateFlow.update {
                        it.copy(
                            isLoading = false,
                            items = it.items + paginatedData,
                            hasNextPage = feed.hasNextPage,
                        )
                    }
                }
                .onFailure { throwable ->
                    _feedStateFlow.update {
                        it.copy(
                            isLoading = false,
                            hasNextPage = false,
                        )
                    }
                    // TODO: logger for each platform
                    println("Error: $throwable")
                }
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}
