package com.dezdeqness.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.feed.contract.model.CatalogFilter
import com.dezdeqness.feed.contract.model.FeedEntity
import com.dezdeqness.feed.contract.repository.FeedRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class FeedViewModel(
    private val feedRepository: FeedRepository,
    private val feedUiMapper: FeedUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val analytics: AkaneAnalytics,
    private val errorReporter: AkaneErrorReporter,
) : ViewModel() {

    private val loadEvents = MutableSharedFlow<LoadEvent>(extraBufferCapacity = 1)

    private val _isFeedFilterShownState = MutableStateFlow(false)
    val isFeedFilterShownState: StateFlow<Boolean> = _isFeedFilterShownState

    @OptIn(ExperimentalCoroutinesApi::class)
    val feedStateFlow: StateFlow<FeedState> = loadEvents
        .onStart { emit(LoadEvent.Initial()) }
        .flatMapLatest { event ->
            flow {
                val result = feedRepository.getFeed(
                    page = event.page,
                    filter = event.input.filterCatalogFilter,
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
                    input = event.input,
                    currentPage = response.currentPage,
                    hasNextPage = response.hasNextPage,
                )
            }

            result.onFailure { error ->
                errorReporter.captureException(
                    throwable = error,
                    message = "Feed load failed",
                    tags = mapOf("feature" to "feed"),
                    extras = mapOf(
                        "page" to event.page.toString(),
                        "search" to event.input.search.orEmpty(),
                    ),
                )
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

    fun onQueryChanged(query: String) {
        val currentInput = feedStateFlow.value.input
        if (currentInput.search == query) return
        val filter = currentInput.filterCatalogFilter.copy(search = query)
        val input = currentInput.copy(filterCatalogFilter = filter)
        loadEvents.tryEmit(LoadEvent.Refresh(input = input))
        analytics.trackSearch(query)
    }

    fun onFilterClicked() {
        _isFeedFilterShownState.update { true }
    }

    fun onFilterChanged(catalogFilter: CatalogFilter) {
        val currentInput = feedStateFlow.value.input

        val input = currentInput.copy(filterCatalogFilter = catalogFilter)
        loadEvents.tryEmit(LoadEvent.Refresh(input = input))
    }

    fun onFilterClosed() {
        _isFeedFilterShownState.update { false }
    }

    fun onLoadMore() {
        val state = feedStateFlow.value
        if (state.hasNextPage) {
            loadEvents.tryEmit(
                LoadEvent.LoadMore(
                    input = state.input,
                    page = state.currentPage + 1,
                )
            )
        }
    }

    fun onRetryClicked() {
        loadEvents.tryEmit(LoadEvent.Refresh(input = feedStateFlow.value.input))
    }

    private sealed class LoadEvent(
        open val input: FeedUserInput,
        open val page: Int,
    ) {
        data class Refresh(
            override val input: FeedUserInput,
        ) : LoadEvent(input = input, page = INITIAL_PAGE)

        data class LoadMore(
            override val input: FeedUserInput,
            override val page: Int,
        ) :
            LoadEvent(input = input, page = page)

        data class Initial(
            override val input: FeedUserInput = FeedUserInput(),
        ) :
            LoadEvent(input = input, page = INITIAL_PAGE)
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
