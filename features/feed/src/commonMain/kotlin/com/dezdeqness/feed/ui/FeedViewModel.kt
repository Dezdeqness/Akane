package com.dezdeqness.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.feed.domain.model.FeedEntity
import com.dezdeqness.feed.domain.repository.FeedRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class FeedViewModel(
    private val feedRepository: FeedRepository,
    private val feedUiMapper: FeedUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    private val loadEvents = MutableSharedFlow<LoadEvent>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val feedStateFlow: StateFlow<FeedState> = loadEvents
        .onStart { emit(LoadEvent.Initial) }
        .flatMapLatest { event ->
            flow {
                val result = feedRepository.getFeed(
                    page = event.page,
                )

                emit(
                    LoadResult(
                        event = event,
                        result = result,
                    )
                )
            }.flowOn(coroutineDispatcherProvider.io())
        }
        .scan(FeedState()) { previous, loadResult ->
            val event = loadResult.event
            val result = loadResult.result

            result.onSuccess { response ->
                val mappedList = response.items.map(feedUiMapper::map)
                val updatedList = when (event) {
                    is LoadEvent.Refresh, is LoadEvent.Initial -> mappedList
                    is LoadEvent.LoadMore -> previous.items + mappedList
                }

                val newStatus = if (mappedList.isEmpty()) {
                    Status.Empty
                } else {
                    Status.Loaded
                }

                return@scan previous.copy(
                    items = updatedList,
                    status = newStatus,
                    currentPage = response.currentPage,
                    hasNextPage = response.hasNextPage,
                )
            }

            result.onFailure { error ->
                val newStatus = when (event) {
                    is LoadEvent.Initial -> Status.Error
                    else -> previous.status
                }

                Logger.e(
                    tag = TAG,
                    messageString = "Load error: ${error.message.orEmpty()}\n$newStatus",
                )

                return@scan previous.copy(
                    status = newStatus,
                )
            }

            previous
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = FeedState()
        )

    fun onLoadMore() {
        val state = feedStateFlow.value
        if (state.hasNextPage) {
            loadEvents.tryEmit(LoadEvent.LoadMore(state.currentPage + 1))
        }
    }

    private sealed class LoadEvent(
        open val page: Int,
    ) {
        data object Refresh : LoadEvent(page = INITIAL_PAGE)

        data class LoadMore(override val page: Int) : LoadEvent(page = page)

        data object Initial : LoadEvent(page = INITIAL_PAGE)
    }

    private data class LoadResult(
        val event: LoadEvent,
        val result: Result<FeedEntity>,
    )

    companion object {
        private const val TAG = "FeedViewModel"
        private const val INITIAL_PAGE = 1
    }
}
