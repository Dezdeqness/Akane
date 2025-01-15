package com.dezdeqness.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
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
                    Logger.i(
                        tag = TAG,
                        messageString = """
                            Initial load current position: $currentPage
                            $feed
                        """.trimIndent(),
                    )
                }
                .onFailure { throwable ->
                    _feedStateFlow.update {
                        it.copy(
                            isLoading = false,
                            hasNextPage = false,
                        )
                    }
                    Logger.e(
                        tag = TAG,
                        messageString = "Initial load: ${throwable.message.orEmpty()}",
                    )
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
                    Logger.i(
                        tag = TAG,
                        messageString = """
                            Load more current position: $currentPage
                            $feed
                        """.trimIndent(),
                    )
                }
                .onFailure { throwable ->
                    _feedStateFlow.update {
                        it.copy(
                            isLoading = false,
                            hasNextPage = false,
                        )
                    }
                    Logger.e(
                        tag = TAG,
                        messageString = "Load more: ${throwable.message.orEmpty()}",
                    )
                }
        }
    }

    companion object {
        private const val TAG = "FeedViewModel"
        private const val INITIAL_PAGE = 1
    }
}
